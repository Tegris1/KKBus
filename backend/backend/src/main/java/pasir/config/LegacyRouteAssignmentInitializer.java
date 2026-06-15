package pasir.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pasir.model.Role;
import pasir.model.Route;
import pasir.model.Schedule;
import pasir.repositories.RouteRepository;
import pasir.repositories.ScheduleRepository;
import pasir.repositories.UserRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LegacyRouteAssignmentInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(LegacyRouteAssignmentInitializer.class);

    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<Route> incompleteRoutes = routeRepository.findAll().stream()
                .filter(route -> route.getDriverId() == null || route.getBusId() == null)
                .toList();
        if (incompleteRoutes.isEmpty()) {
            return;
        }

        Set<Long> driverIds = userRepository.findAllByRoleOrderByUsername(Role.EMPLOYEE).stream()
                .map(user -> user.getId())
                .collect(Collectors.toSet());
        List<Schedule> schedules = scheduleRepository.findAllByOrderByWorkingDateDesc();

        for (Route route : incompleteRoutes) {
            findMatchingSchedule(route, schedules, driverIds).ifPresent(schedule -> {
                if (route.getDriverId() == null) {
                    route.setDriverId(schedule.getEmployeeId());
                }
                if (route.getBusId() == null) {
                    route.setBusId(schedule.getBusId());
                }
                routeRepository.save(route);
                log.info(
                        "Completed legacy route assignment: route={}, driver={}, bus={}",
                        route.getId(),
                        route.getDriverId(),
                        route.getBusId()
                );
            });
        }
    }

    private java.util.Optional<Schedule> findMatchingSchedule(
            Route route,
            List<Schedule> schedules,
            Set<Long> driverIds
    ) {
        if (route.getDepartureTime() == null) {
            return java.util.Optional.empty();
        }

        LocalTime departureTime = route.getDepartureTime().toLocalTime();
        return schedules.stream()
                .filter(schedule -> schedule.getEmployeeId() != null)
                .filter(schedule -> driverIds.contains(schedule.getEmployeeId()))
                .filter(schedule -> schedule.getBusId() != null)
                .filter(schedule -> route.getDepartureTime().getDayOfWeek().equals(schedule.getDayOfWeek()))
                .filter(schedule -> includes(schedule, departureTime))
                .findFirst();
    }

    private boolean includes(Schedule schedule, LocalTime time) {
        if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
            return false;
        }
        return !time.isBefore(schedule.getStartTime()) && !time.isAfter(schedule.getEndTime());
    }
}
