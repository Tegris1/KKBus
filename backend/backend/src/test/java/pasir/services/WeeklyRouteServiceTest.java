package pasir.services;

import org.junit.jupiter.api.Test;
import pasir.model.Route;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeeklyRouteServiceTest {

    private final WeeklyRouteService service = new WeeklyRouteService();

    @Test
    void returnsNextWeeklyOccurrenceForHistoricalRoute() {
        Route route = route(
                LocalDateTime.of(2026, 5, 10, 8, 0),
                LocalDateTime.of(2026, 5, 10, 10, 30)
        );

        var occurrence = service.nextOccurrence(route, LocalDateTime.of(2026, 6, 15, 12, 0));

        assertEquals(LocalDateTime.of(2026, 6, 21, 8, 0), occurrence.departureTime());
        assertEquals(LocalDateTime.of(2026, 6, 21, 10, 30), occurrence.arrivalTime());
        assertTrue(service.isValidOccurrence(route, occurrence.departureTime()));
    }

    private Route route(LocalDateTime departure, LocalDateTime arrival) {
        Route route = new Route();
        route.setDepartureTime(departure);
        route.setArrivalTime(arrival);
        return route;
    }
}
