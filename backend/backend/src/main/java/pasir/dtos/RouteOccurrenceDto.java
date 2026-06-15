package pasir.dtos;

import pasir.model.Route;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RouteOccurrenceDto(
        Long id,
        String origin,
        LocalDateTime departureTime,
        String destination,
        LocalDateTime arrivalTime,
        List<String> intermediateStops,
        BigDecimal price,
        Long driverId,
        Short busId,
        BigDecimal fuelCost
) {
    public static RouteOccurrenceDto from(
            Route route,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime
    ) {
        return new RouteOccurrenceDto(
                route.getId(),
                route.getOrigin(),
                departureTime,
                route.getDestination(),
                arrivalTime,
                route.getIntermediateStops(),
                route.getPrice(),
                route.getDriverId(),
                route.getBusId(),
                route.getFuelCost()
        );
    }
}
