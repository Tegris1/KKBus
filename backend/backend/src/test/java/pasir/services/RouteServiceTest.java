package pasir.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pasir.Mappers.RouteMapper;
import pasir.model.Route;
import pasir.repositories.RouteRepository;
import pasir.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
