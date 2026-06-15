package pasir.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CourseReportDto(
        Long routeId,
        String origin,
        String destination,
        LocalDateTime departureTime,
        Short busId,
        Long driverId,
        String driverName,
        Integer soldTickets,
        Integer passengerCount,
        BigDecimal revenue,
        BigDecimal fuelCost,
        BigDecimal profit,
        List<ReportSegmentDto> segments
) {
}
