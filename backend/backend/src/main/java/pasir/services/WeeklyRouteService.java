package pasir.services;

import org.springframework.stereotype.Component;
import pasir.model.Route;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class WeeklyRouteService {

    public RouteOccurrence nextOccurrence(Route route, LocalDateTime now) {
        LocalDateTime departure = route.getDepartureTime();
        if (!departure.isAfter(now)) {
            long elapsedWeeks = ChronoUnit.WEEKS.between(departure, now);
            departure = departure.plusWeeks(elapsedWeeks);
            if (!departure.isAfter(now)) {
                departure = departure.plusWeeks(1);
            }
        }
        return occurrence(route, departure);
    }

    public List<RouteOccurrence> occurrencesBetween(
            Route route,
            LocalDateTime periodStart,
            LocalDateTime periodEnd
    ) {
        LocalDateTime departure = route.getDepartureTime();
        if (departure.isBefore(periodStart)) {
            long elapsedWeeks = ChronoUnit.WEEKS.between(departure, periodStart);
            departure = departure.plusWeeks(elapsedWeeks);
            if (departure.isBefore(periodStart)) {
                departure = departure.plusWeeks(1);
            }
        }

        List<RouteOccurrence> occurrences = new ArrayList<>();
        while (departure.isBefore(periodEnd)) {
            occurrences.add(occurrence(route, departure));
            departure = departure.plusWeeks(1);
        }
        return occurrences;
    }

    public boolean isValidOccurrence(Route route, LocalDateTime departureTime) {
        if (departureTime.isBefore(route.getDepartureTime())) {
            return false;
        }
        long days = ChronoUnit.DAYS.between(
                route.getDepartureTime().toLocalDate(),
                departureTime.toLocalDate()
        );
        return days % 7 == 0
                && departureTime.toLocalTime().equals(route.getDepartureTime().toLocalTime());
    }

    private RouteOccurrence occurrence(Route route, LocalDateTime departure) {
        Duration duration = Duration.between(route.getDepartureTime(), route.getArrivalTime());
        return new RouteOccurrence(departure, departure.plus(duration));
    }

    public record RouteOccurrence(LocalDateTime departureTime, LocalDateTime arrivalTime) {
    }
}
