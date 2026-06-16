package pasir.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record DriverPassengerCourseDto(
        Long routeId,
        String origin,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        Short busId,
        Integer totalSeats,
        List<DriverPassengerReservationDto> passengers
) {
}
