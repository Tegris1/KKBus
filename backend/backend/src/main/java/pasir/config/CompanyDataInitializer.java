package pasir.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pasir.model.*;
import pasir.repositories.RouteRepository;
import pasir.repositories.UserRepository;
import pasir.repositories.VehicleRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CompanyDataInitializer implements ApplicationRunner {
    private static final String DEFAULT_PASSWORD = "Kkbus123!";

    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedEmployees();
        seedVehicles();
        seedCoreRoutes();
    }

    private void seedEmployees() {
        List.of(
                new EmployeeSeed("tomasz.rajdowiec@kkbus.local", "Tomasz", "Rajdowiec", Role.EMPLOYEE),
                new EmployeeSeed("kazimierz.rajdowiec@kkbus.local", "Kazimierz", "Rajdowiec", Role.EMPLOYEE),
                new EmployeeSeed("miroslaw.szybki@kkbus.local", "Miroslaw", "Szybki", Role.EMPLOYEE),
                new EmployeeSeed("jan.doswiadczony@kkbus.local", "Jan", "Doswiadczony", Role.EMPLOYEE),
                new EmployeeSeed("marek.poprawny@kkbus.local", "Marek", "Poprawny", Role.EMPLOYEE),
                new EmployeeSeed("zuzanna.konkretna@kkbus.local", "Zuzanna", "Konkretna", Role.EMPLOYEE),
                new EmployeeSeed("piotr.uprzejmy@kkbus.local", "Piotr", "Uprzejmy", Role.SECRETARY),
                new EmployeeSeed("agnieszka.mila@kkbus.local", "Agnieszka", "Mila", Role.SECRETARY),
                new EmployeeSeed("jan.kowalski@kkbus.local", "Jan", "Kowalski", Role.ADMIN)
        ).forEach(seed -> userRepository.findByEmail(seed.email()).orElseGet(() -> {
            User user = new User();
            user.setEmail(seed.email());
            user.setUsername(seed.firstName() + " " + seed.lastName());
            user.setFirstName(seed.firstName());
            user.setLastName(seed.lastName());
            user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            user.setRole(seed.role());
            user.setCustomerNumber("KK-" + seed.email().substring(0, 3).toUpperCase() + userRepository.count());
            user.setPoints(0);
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            user.setWallet(wallet);
            return userRepository.save(user);
        }));
    }

    private void seedVehicles() {
        List.of(
                new VehicleSeed((short) 1, "Bus Krakow 1", 20, "Krakow"),
                new VehicleSeed((short) 2, "Bus Krakow 2", 20, "Krakow"),
                new VehicleSeed((short) 3, "Bus Katowice 1", 20, "Katowice"),
                new VehicleSeed((short) 4, "Bus Katowice 2", 20, "Katowice"),
                new VehicleSeed((short) 5, "Autokar KKBus", 50, "Krakow")
        ).forEach(seed -> vehicleRepository.findByFleetNumber(seed.fleetNumber()).orElseGet(() -> {
            Vehicle vehicle = new Vehicle();
            vehicle.setFleetNumber(seed.fleetNumber());
            vehicle.setName(seed.name());
            vehicle.setSeats(seed.seats());
            vehicle.setParkingLocation(seed.parkingLocation());
            vehicle.setStatus(VehicleStatus.ACTIVE);
            vehicle.setAverageFuelConsumption(0.0);
            return vehicleRepository.save(vehicle);
        }));
    }

    private void seedCoreRoutes() {
        if (routeRepository.findByDestinationAndOriginOrderByDepartureTimeDesc("Katowice", "Krakow").isEmpty()
                && routeRepository.findByDestinationAndOriginOrderByDepartureTimeDesc("Katowice", "Kraków").isEmpty()) {
            createRoute("Krakow", List.of("Myslowice", "Jaworzno", "Chrzanow", "Trzebinia"), "Katowice",
                    LocalTime.of(7, 0), "tomasz.rajdowiec@kkbus.local", (short) 1);
        }
        if (routeRepository.findByDestinationAndOriginOrderByDepartureTimeDesc("Krakow", "Katowice").isEmpty()
                && routeRepository.findByDestinationAndOriginOrderByDepartureTimeDesc("Kraków", "Katowice").isEmpty()) {
            createRoute("Katowice", List.of("Chrzanow", "Jaworzno", "Myslowice"), "Krakow",
                    LocalTime.of(8, 0), "kazimierz.rajdowiec@kkbus.local", (short) 3);
        }
    }

    private void createRoute(
            String origin,
            List<String> stops,
            String destination,
            LocalTime departure,
            String driverEmail,
            Short busId
    ) {
        Long driverId = userRepository.findByEmail(driverEmail)
                .map(User::getId)
                .orElse(null);
        Route route = new Route();
        route.setOrigin(origin);
        route.setIntermediateStops(new ArrayList<>(stops));
        route.setDestination(destination);
        route.setDepartureTime(LocalDate.now().plusDays(1).atTime(departure));
        route.setArrivalTime(route.getDepartureTime().plusHours(1).plusMinutes(30));
        route.setPrice(new BigDecimal("15.00"));
        route.setFuelCost(new BigDecimal("70.00"));
        route.setDriverId(driverId);
        route.setBusId(busId);
        routeRepository.save(route);
    }

    private record EmployeeSeed(String email, String firstName, String lastName, Role role) {
    }

    private record VehicleSeed(Short fleetNumber, String name, Integer seats, String parkingLocation) {
    }
}
