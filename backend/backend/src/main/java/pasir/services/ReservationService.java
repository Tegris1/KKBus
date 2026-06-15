package pasir.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pasir.dtos.ReservationDto;
import pasir.dtos.ReservationTicketDto;
import pasir.model.Reservation;
import pasir.model.Route;
import pasir.model.TransactionType;
import pasir.model.User;
import pasir.model.Wallet;
import pasir.repositories.ReservationRepository;
import pasir.repositories.RouteRepository;
import pasir.repositories.UserRepository;
import pasir.repositories.WalletRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
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
        User user = getCurrentUser();
        Route route = routeRepository.findById(reservationDto.getRouteId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono trasy o ID " + reservationDto.getRouteId()));

        int seats = reservationDto.getSeats() == null ? 1 : reservationDto.getSeats();
        if (seats < 1) {
            throw new IllegalArgumentException("Liczba miejsc musi byc wieksza od 0");
        }
        LocalDateTime travelDepartureTime = reservationDto.getTravelDepartureTime();
        if (travelDepartureTime == null || !weeklyRouteService.isValidOccurrence(route, travelDepartureTime)) {
            throw new IllegalArgumentException("Nieprawidlowy termin kursu tygodniowego");
        }
        if (!travelDepartureTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Nie mozna zarezerwowac zakonczonego kursu");
        }
        var occurrence = weeklyRouteService.occurrencesBetween(
                route,
                travelDepartureTime,
                travelDepartureTime.plusNanos(1)
        ).stream().findFirst().orElseThrow(
                () -> new IllegalArgumentException("Nieprawidlowy termin kursu tygodniowego")
        );

        BigDecimal totalPrice = route.getPrice().multiply(BigDecimal.valueOf(seats));
        Wallet wallet = getOrCreateWallet(user);

        if (wallet.getMoney().compareTo(totalPrice) < 0) {
            throw new IllegalArgumentException("Brak wystarczajacych srodkow w portfelu");
        }

        wallet.setMoney(wallet.getMoney().subtract(totalPrice));
        int awardedPoints = totalPrice
                .divide(BigDecimal.TEN, 0, RoundingMode.DOWN)
                .intValueExact();
        int updatedPoints = (wallet.getPoints() == null ? 0 : wallet.getPoints()) + awardedPoints;
        wallet.setPoints(updatedPoints);
        user.setPoints(updatedPoints);
        walletRepository.save(wallet);

        Reservation reservation = new Reservation();
        reservation.setAmount(totalPrice.doubleValue());
        reservation.setType(TransactionType.EXPENSE);
        reservation.setTags("BILET");
        reservation.setNotes(route.getOrigin() + " -> " + route.getDestination());
        reservation.setTimestamp(LocalDateTime.now());
        reservation.setUser(user);
        reservation.setRoute(route);
        reservation.setSeats(seats);
        reservation.setAwardedPoints(awardedPoints);
        reservation.setTravelDepartureTime(occurrence.departureTime());
        reservation.setTravelArrivalTime(occurrence.arrivalTime());

        return reservationRepository.save(reservation);
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
        if (!departureTime.isAfter(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("Rezerwacje mozna anulowac najpozniej 24 godziny przed odjazdem");
        }

        Wallet wallet = getOrCreateWallet(currentUser);
        wallet.setMoney(wallet.getMoney().add(BigDecimal.valueOf(reservation.getAmount())));
        int pointsToReverse = reservation.getAwardedPoints() == null ? 0 : reservation.getAwardedPoints();
        int updatedPoints = (wallet.getPoints() == null ? 0 : wallet.getPoints()) - pointsToReverse;
        wallet.setPoints(updatedPoints);
        currentUser.setPoints(updatedPoints);
        walletRepository.save(wallet);
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
}
