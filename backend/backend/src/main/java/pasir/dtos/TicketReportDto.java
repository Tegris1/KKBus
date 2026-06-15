package pasir.dtos;

import pasir.model.ReportPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TicketReportDto(
        ReportPeriod periodType,
        LocalDate periodStart,
        LocalDate periodEnd,
        LocalDateTime generatedAt,
        Long selectedDriverId,
        Short selectedBusId,
        Integer courseCount,
        Integer soldTickets,
        Integer passengerCount,
        BigDecimal revenue,
        BigDecimal fuelCost,
        BigDecimal profit,
        List<CourseReportDto> courses
) {
}
