package pasir.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pasir.dtos.ReservationDto;
import pasir.dtos.ReservationTicketDto;
import pasir.dtos.DriverPassengerCourseDto;
import pasir.dtos.DriverPassengerReservationDto;
import pasir.model.Reservation;
import pasir.model.Route;
import pasir.model.TransactionType;
import pasir.model.TicketDiscountType;
import pasir.model.User;
import pasir.model.Vehicle;
import pasir.model.Wallet;
import pasir.repositories.ReservationRepository;
import pasir.repositories.RouteRepository;
import pasir.repositories.UserRepository;
import pasir.repositories.VehicleRepository;
import pasir.repositories.WalletRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class ReservationService {
    private static final int DISCOUNT_POINTS_COST = 50;
    private static final BigDecimal POINTS_DISCOUNT_AMOUNT = new BigDecimal("10.00");

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;
    private final WalletRepository walletRepository;
    private final WeeklyRouteService weeklyRouteService;


    public Reservation getTransactionById(Long id) {
        return reservationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono transakcji o ID" + id)
        );
    }

    public Reservation updateTransaction(Long id, ReservationDto reservationDto) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono transakcji o ID" + id)
        );

        if(!(reservation.getUser().getEmail().equals(getCurrentUser().getEmail()))) {
            throw new AccessDeniedException("Nie masz dostepu do tej transakcji");
        }
        if (reservation.getRoute() != null) {
            throw new IllegalArgumentException("Rezerwacji biletu nie mozna edytowac");
        }
        reservation.setAmount(reservationDto.getAmount());
        reservation.setType(reservationDto.getType());
        reservation.setTags(reservationDto.getTags());
        reservation.setNotes(reservationDto.getNotes());

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation createTransaction(ReservationDto reservationDto) {
        if (reservationDto.getRouteId() != null) {
            return createRouteReservation(reservationDto);
        }

        Reservation reservation = new Reservation();
        reservation.setAmount(reservationDto.getAmount());
        reservation.setType(reservationDto.getType());
        reservation.setTags(reservationDto.getTags());
        reservation.setNotes(reservationDto.getNotes());
        reservation.setTimestamp(LocalDateTime.now());
        reservation.setUser(getCurrentUser());
        return reservationRepository.save(reservation);
    }

    private Reservation createRouteReservation(ReservationDto reservationDto) {
        User currentUser = getCurrentUser();
        User user = resolveReservationUser(reservationDto, currentUser);
        validateReservationBlock(user);
        Route route = routeRepository.findByIdForUpdate(reservationDto.getRouteId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono trasy o ID " + reservationDto.getRouteId()));

        int seats = reservationDto.getSeats() == null ? 1 : reservationDto.getSeats();
        if (seats < 1) {
            throw new IllegalArgumentException("Liczba miejsc musi byc wieksza od 0");
        }
        LocalDateTime travelDepartureTime = reservationDto.getTravelDepartureTime();
        if (travelDepartureTime == null || !weeklyRouteService.isValidOccurrence(route, travelDepartureTime)) {
            throw new IllegalArgumentException("Nieprawidlowy termin kursu tygodniowego");
        }
        if (!travelDepartureTime.isAfter(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("Rezerwacje mozna utworzyc najpozniej 2 godziny przed kursem");
        }
        if (travelDepartureTime.isAfter(LocalDateTime.now().plusWeeks(1))) {
            throw new IllegalArgumentException("Rezerwacje mozna skladac maksymalnie tydzien naprzod");
        }
        var occurrence = weeklyRouteService.occurrencesBetween(
                route,
                travelDepartureTime,
                travelDepartureTime.plusNanos(1)
        ).stream().findFirst().orElseThrow(
                () -> new IllegalArgumentException("Nieprawidlowy termin kursu tygodniowego")
        );
        validateSeatAvailability(route, occurrence.departureTime(), seats);

        String boardingStop = normalizeStop(reservationDto.getBoardingStop(), route.getOrigin());
        String alightingStop = normalizeStop(reservationDto.getAlightingStop(), route.getDestination());
        BigDecimal basePrice = calculateSegmentPrice(route, boardingStop, alightingStop);
        BigDecimal discountedPrice = applyPassengerDiscount(basePrice, reservationDto.getDiscountType());
        BigDecimal totalPrice = discountedPrice.multiply(BigDecimal.valueOf(seats));
        Wallet wallet = getOrCreateWalletForUpdate(user);
        boolean usePointsDiscount = Boolean.TRUE.equals(reservationDto.getUsePointsDiscount());
        BigDecimal discountAmount = BigDecimal.ZERO;
        int pointsSpent = 0;

        if (usePointsDiscount) {
            int currentPoints = wallet.getPoints() == null ? 0 : wallet.getPoints();
            if (currentPoints < DISCOUNT_POINTS_COST) {
                throw new IllegalArgumentException("Brak 50 punktow wymaganych do uzycia znizki");
            }
            discountAmount = totalPrice.min(POINTS_DISCOUNT_AMOUNT);
            pointsSpent = DISCOUNT_POINTS_COST;
        }

        BigDecimal priceToPay = totalPrice.subtract(discountAmount);

        if (wallet.getMoney().compareTo(priceToPay) < 0) {
            throw new IllegalArgumentException("Brak wystarczajacych srodkow w portfelu");
        }

        wallet.setMoney(wallet.getMoney().subtract(priceToPay));
        int awardedPoints = priceToPay
                .divide(BigDecimal.TEN, 0, RoundingMode.DOWN)
                .intValueExact();
        int updatedPoints = (wallet.getPoints() == null ? 0 : wallet.getPoints())
                - pointsSpent
                + awardedPoints;
        wallet.setPoints(updatedPoints);
        user.setPoints(updatedPoints);
        walletRepository.save(wallet);

        Reservation reservation = new Reservation();
        reservation.setAmount(priceToPay.doubleValue());
        reservation.setType(TransactionType.EXPENSE);
        reservation.setTags("BILET");
        reservation.setNotes(boardingStop + " -> " + alightingStop);
        reservation.setTimestamp(LocalDateTime.now());
        reservation.setUser(user);
        reservation.setRoute(route);
        reservation.setSeats(seats);
        reservation.setAwardedPoints(awardedPoints);
        reservation.setPointsSpent(pointsSpent);
        reservation.setDiscountAmount(discountAmount);
        reservation.setBoardingStop(boardingStop);
        reservation.setAlightingStop(alightingStop);
        reservation.setDiscountType(reservationDto.getDiscountType() == null
                ? TicketDiscountType.NONE
                : reservationDto.getDiscountType());
        reservation.setTravelDepartureTime(occurrence.departureTime());
        reservation.setTravelArrivalTime(occurrence.arrivalTime());

        return reservationRepository.save(reservation);
    }

    private void validateSeatAvailability(Route route, LocalDateTime travelDepartureTime, int requestedSeats) {
        if (route.getBusId() == null) {
            throw new IllegalArgumentException("Kurs nie ma przypisanego autobusu");
        }

        Vehicle vehicle = vehicleRepository.findByFleetNumber(route.getBusId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono autobusu przypisanego do kursu"));
        int capacity = vehicle.getSeats() == null ? 0 : vehicle.getSeats();
        long reservedSeats = reservationRepository.countReservedSeats(route, travelDepartureTime);
        long availableSeats = capacity - reservedSeats;

        if (requestedSeats > availableSeats) {
            throw new IllegalArgumentException(
                    "Brak wystarczajacej liczby miejsc. Dostepne miejsca: " + Math.max(availableSeats, 0)
            );
        }
    }

    private void validateReservationBlock(User user) {
        LocalDateTime blockedUntil = user.getReservationBlockedUntil();
        if (blockedUntil != null && blockedUntil.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Mozliwosc rezerwacji jest zablokowana do " + blockedUntil);
        }
    }

    private Wallet getOrCreateWallet(User user) {
        if (user.getWallet() != null) {
            return user.getWallet();
        }

        return walletRepository.findByUser(user)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUser(user);
                    wallet.setMoney(BigDecimal.ZERO);
                    wallet.setPoints(user.getPoints() == null ? 0 : user.getPoints());
                    user.setWallet(wallet);
                    return walletRepository.save(wallet);
                });
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Nie istnieje: " + id)
        );
        if(!(reservation.getUser().getEmail().equals(getCurrentUser().getEmail()))) {
            throw new AccessDeniedException("Nie masz dostepu");
        }
        if (reservation.getRoute() != null) {
            cancelReservation(id);
            return;
        }
        reservationRepository.deleteById(id);
    }

    private User resolveReservationUser(ReservationDto dto, User currentUser) {
        if (dto.getPassengerUserId() == null || dto.getPassengerUserId().equals(currentUser.getId())) {
            return currentUser;
        }
        var role = currentUser.getRole();
        if (role != pasir.model.Role.SECRETARY && role != pasir.model.Role.ADMIN) {
            throw new AccessDeniedException("Tylko sekretariat moze rezerwowac dla innego klienta");
        }
        return userRepository.findById(dto.getPassengerUserId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono klienta o ID " + dto.getPassengerUserId()));
    }

    private String normalizeStop(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private BigDecimal calculateSegmentPrice(Route route, String boardingStop, String alightingStop) {
        List<String> stops = new ArrayList<>();
        stops.add(route.getOrigin());
        stops.addAll(route.getIntermediateStops());
        stops.add(route.getDestination());

        int from = stops.indexOf(boardingStop);
        int to = stops.indexOf(alightingStop);
        if (from < 0 || to < 0 || to <= from) {
            throw new IllegalArgumentException("Nieprawidlowy odcinek przejazdu");
        }

        if (stops.size() <= 2) {
            return route.getPrice();
        }

        int segmentCount = stops.size() - 1;
        int selectedSegments = to - from;
        BigDecimal minPrice = new BigDecimal("12.00");
        BigDecimal maxPrice = route.getPrice() == null ? new BigDecimal("15.00") : route.getPrice();
        if (maxPrice.compareTo(minPrice) < 0) {
            return maxPrice;
        }
        return minPrice.add(
                maxPrice.subtract(minPrice)
                        .multiply(BigDecimal.valueOf(selectedSegments))
                        .divide(BigDecimal.valueOf(segmentCount), 2, RoundingMode.HALF_UP)
        );
    }

    private BigDecimal applyPassengerDiscount(BigDecimal price, TicketDiscountType discountType) {
        TicketDiscountType type = discountType == null ? TicketDiscountType.NONE : discountType;
        return switch (type) {
            case CHILD_UNDER_5 -> BigDecimal.ZERO;
            case STUDENT -> price.multiply(new BigDecimal("0.70")).setScale(2, RoundingMode.HALF_UP);
            case NONE -> price;
        };
    }

    private Wallet getOrCreateWalletForUpdate(User user) {
        return walletRepository.findByUserForUpdate(user)
                .orElseGet(() -> getOrCreateWallet(user));
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findByIdForUpdate(id).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono rezerwacji o ID " + id)
        );
        User currentUser = getCurrentUser();

        if (!reservation.getUser().getEmail().equals(currentUser.getEmail())) {
            throw new AccessDeniedException("Nie masz dostepu do tej rezerwacji");
        }
        if (reservation.getRoute() == null) {
            throw new IllegalArgumentException("Ta transakcja nie jest rezerwacja biletu");
        }
        LocalDateTime departureTime = reservation.getTravelDepartureTime() == null
                ? reservation.getRoute().getDepartureTime()
                : reservation.getTravelDepartureTime();
        if (departureTime.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("Rezerwacje mozna anulowac najpozniej 24 godziny przed odjazdem");
        }

        Wallet wallet = getOrCreateWalletForUpdate(currentUser);
        wallet.setMoney(wallet.getMoney().add(BigDecimal.valueOf(reservation.getAmount())));
        int pointsToReverse = reservation.getAwardedPoints() == null ? 0 : reservation.getAwardedPoints();
        int pointsToRefund = reservation.getPointsSpent() == null ? 0 : reservation.getPointsSpent();
        int updatedPoints = (wallet.getPoints() == null ? 0 : wallet.getPoints())
                - pointsToReverse
                + pointsToRefund;
        wallet.setPoints(updatedPoints);
        currentUser.setPoints(updatedPoints);
        walletRepository.save(wallet);
        int cancelledReservations = currentUser.getCancelledReservationsCount() == null
                ? 0
                : currentUser.getCancelledReservationsCount();
        cancelledReservations++;
        if (cancelledReservations >= 3) {
            currentUser.setCancelledReservationsCount(0);
            currentUser.setReservationBlockedUntil(LocalDateTime.now().plusMonths(1));
        } else {
            currentUser.setCancelledReservationsCount(cancelledReservations);
        }
        userRepository.save(currentUser);
        reservationRepository.delete(reservation);
    }

    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException("Uzytkownik nie jest uwierzytelniony");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono zalogowanego użytkownika: " + email));
    }

    public List<Reservation> getAllTransactions(){
        User user = getCurrentUser();
        return reservationRepository.findAllByUser(user);
    }

    public List<ReservationTicketDto> getRouteReservations(){
        User user = getCurrentUser();
        return reservationRepository.findAllByUserAndRouteIsNotNull(user).stream()
                .map(ReservationTicketDto::from)
                .toList();
    }

    public List<DriverPassengerCourseDto> getDriverPassengerLists() {
        User driver = getCurrentUser();
        Map<String, List<Reservation>> reservationsByCourse = new LinkedHashMap<>();

        reservationRepository.findDriverPassengerReservations(driver.getId()).forEach(reservation -> {
            LocalDateTime departureTime = getReservationDepartureTime(reservation);
            String key = reservation.getRoute().getId() + "|" + departureTime;
            reservationsByCourse.computeIfAbsent(key, ignored -> new ArrayList<>()).add(reservation);
        });

        return reservationsByCourse.values().stream()
                .map(this::toDriverPassengerCourseDto)
                .toList();
    }

    private DriverPassengerCourseDto toDriverPassengerCourseDto(List<Reservation> reservations) {
        Reservation firstReservation = reservations.getFirst();
        Route route = firstReservation.getRoute();
        Integer totalSeats = route.getBusId() == null
                ? null
                : vehicleRepository.findByFleetNumber(route.getBusId())
                        .map(Vehicle::getSeats)
                        .orElse(null);

        return new DriverPassengerCourseDto(
                route.getId(),
                route.getOrigin(),
                route.getDestination(),
                getReservationDepartureTime(firstReservation),
                firstReservation.getTravelArrivalTime() == null
                        ? route.getArrivalTime()
                        : firstReservation.getTravelArrivalTime(),
                route.getBusId(),
                totalSeats,
                reservations.stream()
                        .map(this::toDriverPassengerReservationDto)
                        .toList()
        );
    }

    private DriverPassengerReservationDto toDriverPassengerReservationDto(Reservation reservation) {
        User passenger = reservation.getUser();
        return new DriverPassengerReservationDto(
                reservation.getId(),
                passengerName(passenger),
                passenger.getEmail(),
                passenger.getPhoneNumber(),
                reservation.getSeats() == null ? 1 : reservation.getSeats(),
                reservation.getBoardingStop(),
                reservation.getAlightingStop(),
                reservation.getDiscountType() == null ? null : reservation.getDiscountType().name()
        );
    }

    private LocalDateTime getReservationDepartureTime(Reservation reservation) {
        return reservation.getTravelDepartureTime() == null
                ? reservation.getRoute().getDepartureTime()
                : reservation.getTravelDepartureTime();
    }

    private String passengerName(User passenger) {
        String fullName = String.join(
                " ",
                List.of(passenger.getFirstName(), passenger.getLastName()).stream()
                        .filter(value -> value != null && !value.isBlank())
                        .toList()
        ).trim();

        if (!fullName.isBlank()) {
            return fullName;
        }

        return passenger.getUsername();
    }
}
