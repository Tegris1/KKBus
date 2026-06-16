package pasir.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pasir.Mappers.RouteMapper;
import pasir.dtos.RouteDto;
import pasir.model.Role;
import pasir.model.Route;
import pasir.model.User;
import pasir.model.Vehicle;
import pasir.repositories.ReservationRepository;
import pasir.repositories.RouteRepository;
import pasir.repositories.UserRepository;
import pasir.repositories.VehicleRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;
    @Mock
    private RouteMapper routeMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private ReservationRepository reservationRepository;

    @Test
    void historicalRouteIsReturnedAsNextWeeklyOccurrenceWithAvailableSeats() {
        Route route = new Route();
        route.setId(1L);
        route.setOrigin("Krakow");
        route.setDestination("Warszawa");
        route.setDepartureTime(LocalDateTime.of(2026, 5, 10, 8, 0));
        route.setArrivalTime(LocalDateTime.of(2026, 5, 10, 10, 30));
        route.setBusId((short) 101);

        Vehicle vehicle = new Vehicle();
        vehicle.setFleetNumber(route.getBusId());
        vehicle.setSeats(20);

        when(routeRepository.findByDestinationAndOriginOrderByDepartureTimeDesc("Warszawa", "Krakow"))
                .thenReturn(List.of(route));
        when(vehicleRepository.findByFleetNumber(route.getBusId())).thenReturn(Optional.of(vehicle));
        when(reservationRepository.countReservedSeats(any(Route.class), any(LocalDateTime.class))).thenReturn(6L);

        RouteService service = service();

        var result = service.findAllByDestinationAndOrigin("Warszawa", "Krakow");

        assertEquals(1, result.size());
        assertEquals("Krakow", result.getFirst().origin());
        assertEquals("Warszawa", result.getFirst().destination());
        assertEquals(8, result.getFirst().departureTime().getHour());
        assertEquals(7, result.getFirst().departureTime().getDayOfWeek().getValue());
        assertEquals(20, result.getFirst().busSeats());
        assertEquals(6L, result.getFirst().reservedSeats());
        assertEquals(14, result.getFirst().availableSeats());
    }

    @Test
    void createRouteAlwaysCopiesDriverBusAndFuelCostToEntity() {
        User driver = new User();
        driver.setId(3L);
        driver.setRole(Role.EMPLOYEE);

        RouteDto dto = new RouteDto();
        dto.setDriverId(driver.getId());
        dto.setBusId((short) 101);
        dto.setFuelCost(new BigDecimal("120.00"));

        Route mappedRoute = new Route();
        when(userRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(routeMapper.toEntity(dto)).thenReturn(mappedRoute);
        when(routeRepository.save(any(Route.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Route savedRoute = service().createRoute(dto);

        assertEquals(3L, savedRoute.getDriverId());
        assertEquals((short) 101, savedRoute.getBusId());
        assertEquals(0, new BigDecimal("120.00").compareTo(savedRoute.getFuelCost()));
        verify(routeRepository).save(mappedRoute);
    }

    private RouteService service() {
        return new RouteService(
                routeRepository,
                routeMapper,
                userRepository,
                vehicleRepository,
                reservationRepository,
                new WeeklyRouteService()
        );
    }
}
