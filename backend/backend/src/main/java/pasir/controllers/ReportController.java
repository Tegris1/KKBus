package pasir.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pasir.dtos.ReportOptionsDto;
import pasir.dtos.TicketReportDto;
import pasir.model.ReportPeriod;
import pasir.services.ReportService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/tickets")
    public ResponseEntity<TicketReportDto> getTicketReport(
            @RequestParam ReportPeriod period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) Short busId
    ) {
        return ResponseEntity.ok(reportService.generate(period, referenceDate, driverId, busId));
    }

    @GetMapping("/options")
    public ResponseEntity<ReportOptionsDto> getReportOptions() {
        return ResponseEntity.ok(reportService.getOptions());
    }
}
