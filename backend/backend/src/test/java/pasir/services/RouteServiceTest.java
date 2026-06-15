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
import pasir.repositories.RouteRepository;
import pasir.repositories.UserRepository;

import java.time.LocalDateTime;
import java.math.BigDecimal;
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

    @Test
    void historicalRouteIsReturnedAsNextWeeklyOccurrence() {
        Route route = new Route();
        route.setId(1L);
        route.setOrigin("Kraków");
        route.setDestination("Warszawa");
        route.setDepartureTime(LocalDateTime.of(2026, 5, 10, 8, 0));
        route.setArrivalTime(LocalDateTime.of(2026, 5, 10, 10, 30));

        when(routeRepository.findByDestinationAndOriginOrderByDepartureTimeDesc("Warszawa", "Kraków"))
                .thenReturn(List.of(route));

        RouteService service = new RouteService(
                routeRepository,
                routeMapper,
                userRepository,
                new WeeklyRouteService()
        );

        var result = service.findAllByDestinationAndOrigin("Warszawa", "Kraków");

        assertEquals(1, result.size());
        assertEquals("Kraków", result.getFirst().origin());
        assertEquals("Warszawa", result.getFirst().destination());
        assertEquals(8, result.getFirst().departureTime().getHour());
        assertEquals(7, result.getFirst().departureTime().getDayOfWeek().getValue());
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

        RouteService service = new RouteService(
                routeRepository,
                routeMapper,
                userRepository,
                new WeeklyRouteService()
        );

        Route savedRoute = service.createRoute(dto);

        assertEquals(3L, savedRoute.getDriverId());
        assertEquals((short) 101, savedRoute.getBusId());
        assertEquals(0, new BigDecimal("120.00").compareTo(savedRoute.getFuelCost()));
        verify(routeRepository).save(mappedRoute);
    }
}
