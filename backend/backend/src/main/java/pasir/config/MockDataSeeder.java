package pasir.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pasir.model.Reservation;
import pasir.model.Role;
import pasir.model.Route;
import pasir.model.Schedule;
import pasir.model.TransactionType;
import pasir.model.User;
import pasir.model.Wallet;
import pasir.repositories.ReservationRepository;
import pasir.repositories.RouteRepository;
import pasir.repositories.ScheduleRepository;
import pasir.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.mock-data.enabled", havingValue = "true")
public class MockDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MockDataSeeder.class);
    private static final String MOCK_PASSWORD = "Mock123!";
    private static final List<Short> BUS_IDS = List.of((short) 101, (short) 102, (short) 103);
    private static final List<RouteTemplate> ROUTE_TEMPLATES = List.of(
            new RouteTemplate("Kraków", List.of("Katowice"), "Wrocław", "45.00", "135.00"),
            new RouteTemplate("Warszawa", List.of("Łódź"), "Poznań", "55.00", "165.00"),
            new RouteTemplate("Gdańsk", List.of("Bydgoszcz"), "Warszawa", "60.00", "180.00"),
            new RouteTemplate("Katowice", List.of("Kraków"), "Rzeszów", "38.00", "115.00")
    );

    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<User> drivers = createDrivers();
        List<User> passengers = createPassengers();
        createSecretary();

        LocalDate startDate = LocalDate.now().minusDays(13);
        int schedulesCreated = 0;
        int ticketsCreated = 0;

        for (int dayIndex = 0; dayIndex < 14; dayIndex++) {
            LocalDate serviceDate = startDate.plusDays(dayIndex);

            for (int tripIndex = 0; tripIndex < 2; tripIndex++) {
                int sequence = dayIndex * 2 + tripIndex;
                User driver = drivers.get(sequence % drivers.size());
                Short busId = BUS_IDS.get(sequence % BUS_IDS.size());
                RouteTemplate template = ROUTE_TEMPLATES.get(sequence % ROUTE_TEMPLATES.size());
                LocalTime departureTime = tripIndex == 0 ? LocalTime.of(7, 30) : LocalTime.of(15, 30);
                LocalDateTime departure = LocalDateTime.of(serviceDate, departureTime);

                Route route = findOrCreateRoute(template, departure, driver, busId);
                if (createScheduleIfMissing(route, driver)) {
                    schedulesCreated++;
                }
                ticketsCreated += createTickets(route, passengers, sequence);
            }
        }

        log.info(
                "Mock data ready for {} through {}: {} drivers, {} buses, {} routes, {} schedules, {} tickets",
                startDate,
                LocalDate.now(),
                drivers.size(),
                BUS_IDS.size(),
                routeRepository.findAllByDepartureTimeGreaterThanEqualAndDepartureTimeLessThanOrderByDepartureTime(
                        startDate.atStartOfDay(),
                        LocalDate.now().plusDays(1).atStartOfDay()
                ).size(),
                schedulesCreated,
                ticketsCreated
        );
    }

    private List<User> createDrivers() {
        return List.of(
                findOrCreateUser("mock.driver1@kkbus.local", "Jan Kowalski", Role.EMPLOYEE),
                findOrCreateUser("mock.driver2@kkbus.local", "Anna Nowak", Role.EMPLOYEE),
                findOrCreateUser("mock.driver3@kkbus.local", "Piotr Zielinski", Role.EMPLOYEE)
        );
    }

    private List<User> createPassengers() {
        List<User> passengers = new ArrayList<>();
        for (int index = 1; index <= 8; index++) {
            passengers.add(findOrCreateUser(
                    "mock.passenger" + index + "@kkbus.local",
                    "Pasazer Mock " + index,
                    Role.USER
            ));
        }
        return passengers;
    }

    private void createSecretary() {
        findOrCreateUser("mock.secretary@kkbus.local", "Sekretarka Mock", Role.SECRETARY);
    }

    private User findOrCreateUser(String email, String username, Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(MOCK_PASSWORD));
            user.setRole(role);
            user.setPoints(0);

            Wallet wallet = new Wallet();
            wallet.setUser(user);
            wallet.setMoney(new BigDecimal("500.00"));
            wallet.setPoints(0);
            user.setWallet(wallet);
            return userRepository.save(user);
        });
    }

    private Route findOrCreateRoute(
            RouteTemplate template,
            LocalDateTime departure,
            User driver,
            Short busId
    ) {
        return routeRepository
                .findAllByDepartureTimeGreaterThanEqualAndDepartureTimeLessThanOrderByDepartureTime(
                        departure,
                        departure.plusMinutes(1)
                ).stream()
                .filter(route -> busId.equals(route.getBusId()))
                .findFirst()
                .orElseGet(() -> {
                    Route route = new Route();
                    route.setOrigin(template.origin());
                    route.setIntermediateStops(new ArrayList<>(template.intermediateStops()));
                    route.setDestination(template.destination());
                    route.setDepartureTime(departure);
                    route.setArrivalTime(departure.plusHours(3));
                    route.setPrice(new BigDecimal(template.ticketPrice()));
                    route.setFuelCost(new BigDecimal(template.fuelCost()));
                    route.setDriverId(driver.getId());
                    route.setBusId(busId);
                    return routeRepository.save(route);
                });
    }

    private boolean createScheduleIfMissing(Route route, User driver) {
        LocalDate workingDate = route.getDepartureTime().toLocalDate();
        if (scheduleRepository.existsByEmployeeIdAndBusIdAndWorkingDate(
                driver.getId(), route.getBusId(), workingDate
        )) {
            return false;
        }

        Schedule schedule = new Schedule();
        schedule.setEmployeeId(driver.getId());
        schedule.setBusId(route.getBusId());
        schedule.setWorkingDate(workingDate);
        schedule.setDayOfWeek(workingDate.getDayOfWeek());
        schedule.setStartTime(route.getDepartureTime().toLocalTime().minusHours(1));
        schedule.setEndTime(route.getArrivalTime().toLocalTime().plusHours(1));
        scheduleRepository.save(schedule);
        return true;
    }

    private int createTickets(Route route, List<User> passengers, int sequence) {
        int ticketCount = 3 + sequence % 3;
        int created = 0;

        for (int ticketIndex = 0; ticketIndex < ticketCount; ticketIndex++) {
            User passenger = passengers.get((sequence + ticketIndex) % passengers.size());
            if (reservationRepository.existsByUserAndRoute(passenger, route)) {
                continue;
            }

            int seats = 1 + (sequence + ticketIndex) % 3;
            Reservation reservation = new Reservation();
            reservation.setUser(passenger);
            reservation.setRoute(route);
            reservation.setSeats(seats);
            reservation.setAmount(route.getPrice().multiply(BigDecimal.valueOf(seats)).doubleValue());
            reservation.setType(TransactionType.EXPENSE);
            reservation.setTags("BILET_MOCK");
            reservation.setNotes(route.getOrigin() + " -> " + route.getDestination());
            reservation.setTimestamp(route.getDepartureTime().minusHours(8L + ticketIndex * 3L));
            reservationRepository.save(reservation);
            created++;
        }
        return created;
    }

    private record RouteTemplate(
            String origin,
            List<String> intermediateStops,
            String destination,
            String ticketPrice,
            String fuelCost
    ) {
    }
}
