package pasir.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pasir.dtos.CourseReportDto;
import pasir.dtos.DriverOptionDto;
import pasir.dtos.ReportOptionsDto;
import pasir.dtos.ReportSegmentDto;
import pasir.dtos.TicketReportDto;
import pasir.model.ReportPeriod;
import pasir.model.Reservation;
import pasir.model.Role;
import pasir.model.Route;
import pasir.repositories.ReservationRepository;
import pasir.repositories.RouteRepository;
import pasir.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final RouteRepository routeRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final WeeklyRouteService weeklyRouteService;

    @Transactional(readOnly = true)
    public TicketReportDto generate(
            ReportPeriod period,
            LocalDate referenceDate,
            Long driverId,
            Short busId
    ) {
        DateRange range = resolveRange(period, referenceDate);
        List<Route> routes = routeRepository.findAll().stream()
                .filter(route -> driverId == null || driverId.equals(route.getDriverId()))
                .filter(route -> busId == null || busId.equals(route.getBusId()))
                .toList();

        List<Reservation> reservations = routes.isEmpty()
                ? List.of()
                : reservationRepository.findAllByRouteIn(routes);
        Map<OccurrenceKey, List<Reservation>> reservationsByOccurrence = reservations.isEmpty()
                ? Collections.emptyMap()
                : reservations.stream().collect(Collectors.groupingBy(reservation -> new OccurrenceKey(
                        reservation.getRoute().getId(),
                        reservation.getTravelDepartureTime() == null
                                ? reservation.getRoute().getDepartureTime()
                                : reservation.getTravelDepartureTime()
                )));
        Map<Long, String> driverNames = userRepository.findAllByRoleOrderByUsername(Role.EMPLOYEE).stream()
                .collect(Collectors.toMap(user -> user.getId(), user -> user.getUsername()));

        List<CourseReportDto> courses = routes.stream()
                .flatMap(route -> weeklyRouteService.occurrencesBetween(
                        route,
                        range.start().atStartOfDay(),
                        range.endExclusive().atStartOfDay()
                ).stream().map(occurrence -> buildCourseReport(
                        route,
                        occurrence,
                        reservationsByOccurrence.getOrDefault(
                                new OccurrenceKey(route.getId(), occurrence.departureTime()),
                                List.of()
                        ),
                        driverNames.getOrDefault(route.getDriverId(), "Nieznany kierowca")
                )))
                .sorted((left, right) -> left.departureTime().compareTo(right.departureTime()))
                .toList();

        int soldTickets = courses.stream().mapToInt(CourseReportDto::soldTickets).sum();
        int passengerCount = courses.stream().mapToInt(CourseReportDto::passengerCount).sum();
        BigDecimal revenue = sum(courses, CourseReportDto::revenue);
        BigDecimal fuelCost = sum(courses, CourseReportDto::fuelCost);

        return new TicketReportDto(
                period,
                range.start(),
                range.endExclusive().minusDays(1),
                LocalDateTime.now(),
                driverId,
                busId,
                courses.size(),
                soldTickets,
                passengerCount,
                revenue,
                fuelCost,
                revenue.subtract(fuelCost),
                courses
        );
    }

    @Transactional(readOnly = true)
    public ReportOptionsDto getOptions() {
        List<DriverOptionDto> drivers = userRepository.findAllByRoleOrderByUsername(Role.EMPLOYEE).stream()
                .map(DriverOptionDto::from)
                .toList();
        List<Short> busIds = routeRepository.findAll().stream()
                .map(Route::getBusId)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
        return new ReportOptionsDto(drivers, busIds);
    }

    private CourseReportDto buildCourseReport(
            Route route,
            WeeklyRouteService.RouteOccurrence occurrence,
            List<Reservation> reservations,
            String driverName
    ) {
        int passengers = reservations.stream()
                .map(Reservation::getSeats)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
        BigDecimal revenue = reservations.stream()
                .map(Reservation::getAmount)
                .filter(Objects::nonNull)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal fuelCost = route.getFuelCost() == null ? BigDecimal.ZERO : route.getFuelCost();

        List<String> stops = new ArrayList<>();
        stops.add(route.getOrigin());
        stops.addAll(route.getIntermediateStops());
        stops.add(route.getDestination());

        List<ReportSegmentDto> segments = new ArrayList<>();
        for (int index = 0; index < stops.size() - 1; index++) {
            segments.add(new ReportSegmentDto(stops.get(index), stops.get(index + 1), passengers));
        }

        return new CourseReportDto(
                route.getId(),
                route.getOrigin(),
                route.getDestination(),
                occurrence.departureTime(),
                route.getBusId(),
                route.getDriverId(),
                driverName,
                reservations.size(),
                passengers,
                revenue,
                fuelCost,
                revenue.subtract(fuelCost),
                segments
        );
    }

    private BigDecimal sum(
            List<CourseReportDto> courses,
            Function<CourseReportDto, BigDecimal> mapper
    ) {
        return courses.stream().map(mapper).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private DateRange resolveRange(ReportPeriod period, LocalDate referenceDate) {
        LocalDate start = switch (period) {
            case DAILY -> referenceDate;
            case WEEKLY -> referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case MONTHLY -> referenceDate.withDayOfMonth(1);
            case YEARLY -> referenceDate.withDayOfYear(1);
        };
        LocalDate endExclusive = switch (period) {
            case DAILY -> start.plusDays(1);
            case WEEKLY -> start.plusWeeks(1);
            case MONTHLY -> start.plusMonths(1);
            case YEARLY -> start.plusYears(1);
        };
        return new DateRange(start, endExclusive);
    }

    private record DateRange(LocalDate start, LocalDate endExclusive) {
    }

    private record OccurrenceKey(Long routeId, LocalDateTime departureTime) {
    }
}
