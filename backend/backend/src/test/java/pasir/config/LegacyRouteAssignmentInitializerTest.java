package pasir.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pasir.model.Role;
import pasir.model.Route;
import pasir.model.Schedule;
import pasir.model.User;
import pasir.repositories.RouteRepository;
import pasir.repositories.ScheduleRepository;
import pasir.repositories.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LegacyRouteAssignmentInitializerTest {
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void fillsMissingAssignmentFromLatestMatchingWeeklySchedule() {
        Route route = new Route();
        route.setId(1L);
        route.setDepartureTime(LocalDateTime.of(2026, 5, 10, 8, 0));

        User driver = new User();
        driver.setId(6L);
        driver.setRole(Role.EMPLOYEE);

        Schedule schedule = new Schedule();
        schedule.setEmployeeId(driver.getId());
        schedule.setBusId((short) 101);
        schedule.setWorkingDate(LocalDate.of(2026, 6, 14));
        schedule.setDayOfWeek(DayOfWeek.SUNDAY);
        schedule.setStartTime(LocalTime.of(6, 30));
        schedule.setEndTime(LocalTime.of(11, 30));

        when(routeRepository.findAll()).thenReturn(List.of(route));
        when(userRepository.findAllByRoleOrderByUsername(Role.EMPLOYEE)).thenReturn(List.of(driver));
        when(scheduleRepository.findAllByOrderByWorkingDateDesc()).thenReturn(List.of(schedule));

        new LegacyRouteAssignmentInitializer(
                routeRepository,
                scheduleRepository,
                userRepository
        ).run(null);

        assertEquals(6L, route.getDriverId());
        assertEquals((short) 101, route.getBusId());
        verify(routeRepository).save(route);
    }
}
