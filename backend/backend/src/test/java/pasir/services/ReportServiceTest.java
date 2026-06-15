package pasir.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pasir.dtos.TicketReportDto;
import pasir.model.ReportPeriod;
import pasir.model.Reservation;
import pasir.model.Role;
import pasir.model.Route;
import pasir.model.User;
import pasir.repositories.ReservationRepository;
import pasir.repositories.RouteRepository;
import pasir.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private RouteRepository routeRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void generatesMonthlyCourseReportWithSegmentsAndFinancialSummary() {
        LocalDate referenceDate = LocalDate.of(2026, 6, 15);
        User driver = driver();
        Route route = route(driver.getId());
        List<Reservation> reservations = List.of(
                reservation(route, 2, 60.0),
                reservation(route, 1, 30.0)
        );

        when(routeRepository.findAllByDepartureTimeGreaterThanEqualAndDepartureTimeLessThanOrderByDepartureTime(
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2026, 7, 1, 0, 0)
        )).thenReturn(List.of(route));
        when(reservationRepository.findAllByRouteIn(List.of(route))).thenReturn(reservations);
        when(userRepository.findAllByRoleOrderByUsername(Role.EMPLOYEE)).thenReturn(List.of(driver));

        TicketReportDto report = service().generate(ReportPeriod.MONTHLY, referenceDate, null, null);

        assertEquals(LocalDate.of(2026, 6, 1), report.periodStart());
        assertEquals(LocalDate.of(2026, 6, 30), report.periodEnd());
        assertEquals(1, report.courseCount());
        assertEquals(2, report.soldTickets());
        assertEquals(3, report.passengerCount());
        assertMoney("90.00", report.revenue());
        assertMoney("40.00", report.fuelCost());
        assertMoney("50.00", report.profit());
        assertEquals(2, report.courses().getFirst().segments().size());
        assertEquals(3, report.courses().getFirst().segments().getFirst().passengerCount());
        assertEquals("Jan Kowalski", report.courses().getFirst().driverName());
    }

    private ReportService service() {
        return new ReportService(routeRepository, reservationRepository, userRepository);
    }

    private User driver() {
        User driver = new User();
        driver.setId(7L);
        driver.setUsername("Jan Kowalski");
        driver.setRole(Role.EMPLOYEE);
        return driver;
    }

    private Route route(Long driverId) {
        Route route = new Route();
        route.setId(11L);
        route.setOrigin("Krakow");
        route.setIntermediateStops(List.of("Katowice"));
        route.setDestination("Wroclaw");
        route.setDepartureTime(LocalDateTime.of(2026, 6, 20, 8, 0));
        route.setDriverId(driverId);
        route.setBusId((short) 101);
        route.setFuelCost(new BigDecimal("40.00"));
        return route;
    }

    private Reservation reservation(Route route, int seats, double amount) {
        Reservation reservation = new Reservation();
        reservation.setRoute(route);
        reservation.setSeats(seats);
        reservation.setAmount(amount);
        return reservation;
    }

    private void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
