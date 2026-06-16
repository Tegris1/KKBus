package pasir.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import pasir.model.Reservation;
import pasir.model.Route;
import pasir.model.User;
import pasir.model.Vehicle;
import pasir.model.Wallet;
import pasir.dtos.ReservationDto;
import pasir.repositories.ReservationRepository;
import pasir.repositories.RouteRepository;
import pasir.repositories.UserRepository;
import pasir.repositories.VehicleRepository;
import pasir.repositories.WalletRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private WalletRepository walletRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void cancelReservationRefundsPaymentAndDeletesReservation() {
        User user = authenticatedUser();
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setMoney(new BigDecimal("100.00"));
        wallet.setPoints(10);
        user.setWallet(wallet);

        Reservation reservation = routeReservation(user, LocalDateTime.now().plusDays(2));
        ReservationService service = service();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findByIdForUpdate(reservation.getId())).thenReturn(Optional.of(reservation));

        service.cancelReservation(reservation.getId());

        assertEquals(0, new BigDecimal("130.00").compareTo(wallet.getMoney()));
        assertEquals(7, wallet.getPoints());
        verify(walletRepository).save(wallet);
        verify(reservationRepository).delete(reservation);
    }

    @Test
    void createRouteReservationAwardsTenPercentOfTicketValueAsWholePoints() {
        User user = authenticatedUser();
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setMoney(new BigDecimal("100.00"));
        wallet.setPoints(2);
        user.setWallet(wallet);

        LocalDateTime departure = LocalDateTime.now().plusDays(2).withNano(0);
        Route route = new Route();
        route.setId(5L);
        route.setOrigin("Krakow");
        route.setDestination("Warszawa");
        route.setDepartureTime(departure);
        route.setArrivalTime(departure.plusHours(4));
        route.setPrice(new BigDecimal("50.00"));
        route.setBusId((short) 101);

        ReservationDto dto = new ReservationDto();
        dto.setRouteId(route.getId());
        dto.setSeats(1);
        dto.setTravelDepartureTime(departure);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(routeRepository.findByIdForUpdate(route.getId())).thenReturn(Optional.of(route));
        when(vehicleRepository.findByFleetNumber(route.getBusId())).thenReturn(Optional.of(vehicle(route.getBusId(), 20)));
        when(reservationRepository.countReservedSeats(route, departure)).thenReturn(0L);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Reservation reservation = service().createTransaction(dto);

        assertEquals(0, new BigDecimal("50.00").compareTo(wallet.getMoney()));
        assertEquals(7, wallet.getPoints());
        assertEquals(7, user.getPoints());
        assertEquals(5, reservation.getAwardedPoints());
        verify(walletRepository).save(wallet);
    }

    @Test
    void createRouteReservationAppliesPointsDiscountAndAwardsPointsForPaidAmount() {
        User user = authenticatedUser();
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setMoney(new BigDecimal("100.00"));
        wallet.setPoints(60);
        user.setWallet(wallet);

        LocalDateTime departure = LocalDateTime.now().plusDays(2).withNano(0);
        Route route = route(8L, departure, "50.00");
        ReservationDto dto = reservationDto(route, departure);
        dto.setUsePointsDiscount(true);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(routeRepository.findByIdForUpdate(route.getId())).thenReturn(Optional.of(route));
        when(vehicleRepository.findByFleetNumber(route.getBusId())).thenReturn(Optional.of(vehicle(route.getBusId(), 20)));
        when(reservationRepository.countReservedSeats(route, departure)).thenReturn(0L);
        when(walletRepository.findByUserForUpdate(user)).thenReturn(Optional.of(wallet));
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Reservation reservation = service().createTransaction(dto);

        assertEquals(0, new BigDecimal("60.00").compareTo(wallet.getMoney()));
        assertEquals(14, wallet.getPoints());
        assertEquals(40.0, reservation.getAmount());
        assertEquals(0, new BigDecimal("10.00").compareTo(reservation.getDiscountAmount()));
        assertEquals(50, reservation.getPointsSpent());
        assertEquals(4, reservation.getAwardedPoints());
    }

    @Test
    void createRouteReservationRejectsPointsDiscountWhenPointsAreInsufficient() {
        User user = authenticatedUser();
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setMoney(new BigDecimal("100.00"));
        wallet.setPoints(49);
        user.setWallet(wallet);

        LocalDateTime departure = LocalDateTime.now().plusDays(2).withNano(0);
        Route route = route(9L, departure, "50.00");
        ReservationDto dto = reservationDto(route, departure);
        dto.setUsePointsDiscount(true);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(routeRepository.findByIdForUpdate(route.getId())).thenReturn(Optional.of(route));
        when(vehicleRepository.findByFleetNumber(route.getBusId())).thenReturn(Optional.of(vehicle(route.getBusId(), 20)));
        when(reservationRepository.countReservedSeats(route, departure)).thenReturn(0L);
        when(walletRepository.findByUserForUpdate(user)).thenReturn(Optional.of(wallet));

        assertThrows(IllegalArgumentException.class, () -> service().createTransaction(dto));

        assertEquals(49, wallet.getPoints());
        assertEquals(0, new BigDecimal("100.00").compareTo(wallet.getMoney()));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createRouteReservationRejectsPurchaseAboveVehicleCapacity() {
        User user = authenticatedUser();
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setMoney(new BigDecimal("100.00"));
        wallet.setPoints(0);
        user.setWallet(wallet);

        LocalDateTime departure = LocalDateTime.now().plusDays(2).withNano(0);
        Route route = route(11L, departure, "50.00");
        ReservationDto dto = reservationDto(route, departure);
        dto.setSeats(3);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(routeRepository.findByIdForUpdate(route.getId())).thenReturn(Optional.of(route));
        when(vehicleRepository.findByFleetNumber(route.getBusId())).thenReturn(Optional.of(vehicle(route.getBusId(), 20)));
        when(reservationRepository.countReservedSeats(route, departure)).thenReturn(18L);

        assertThrows(IllegalArgumentException.class, () -> service().createTransaction(dto));

        assertEquals(0, new BigDecimal("100.00").compareTo(wallet.getMoney()));
        verify(walletRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createRouteReservationRejectsWhenUserReservationBlockIsActive() {
        User user = authenticatedUser();
        user.setReservationBlockedUntil(LocalDateTime.now().plusDays(10));

        ReservationDto dto = new ReservationDto();
        dto.setRouteId(12L);
        dto.setSeats(1);
        dto.setTravelDepartureTime(LocalDateTime.now().plusDays(2));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> service().createTransaction(dto));

        verify(routeRepository, never()).findByIdForUpdate(any());
        verify(walletRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void thirdCancellationBlocksReservationForOneMonth() {
        User user = authenticatedUser();
        user.setCancelledReservationsCount(2);
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setMoney(new BigDecimal("100.00"));
        wallet.setPoints(10);
        user.setWallet(wallet);

        Reservation reservation = routeReservation(user, LocalDateTime.now().plusDays(2));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findByIdForUpdate(reservation.getId())).thenReturn(Optional.of(reservation));

        service().cancelReservation(reservation.getId());

        assertEquals(0, user.getCancelledReservationsCount());
        assertTrue(user.getReservationBlockedUntil().isAfter(LocalDateTime.now().plusDays(29)));
        verify(userRepository).save(user);
        verify(reservationRepository).delete(reservation);
    }

    @Test
    void cancelReservationRejectsCancellationWithinTwentyFourHours() {
        User user = authenticatedUser();
        Reservation reservation = routeReservation(user, LocalDateTime.now().plusHours(23));
        ReservationService service = service();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findByIdForUpdate(reservation.getId())).thenReturn(Optional.of(reservation));

        assertThrows(IllegalArgumentException.class, () -> service.cancelReservation(reservation.getId()));

        verify(walletRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(reservationRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    private ReservationService service() {
        return new ReservationService(
                reservationRepository,
                userRepository,
                routeRepository,
                vehicleRepository,
                walletRepository,
                new WeeklyRouteService()
        );
    }

    private User authenticatedUser() {
        User user = new User();
        user.setEmail("user@example.com");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null)
        );
        return user;
    }

    private Reservation routeReservation(User user, LocalDateTime departureTime) {
        Route route = new Route();
        route.setDepartureTime(departureTime);

        Reservation reservation = new Reservation();
        reservation.setId(10L);
        reservation.setUser(user);
        reservation.setRoute(route);
        reservation.setAmount(30.0);
        reservation.setAwardedPoints(3);
        return reservation;
    }

    private Route route(Long id, LocalDateTime departure, String price) {
        Route route = new Route();
        route.setId(id);
        route.setOrigin("Krakow");
        route.setDestination("Warszawa");
        route.setDepartureTime(departure);
        route.setArrivalTime(departure.plusHours(4));
        route.setPrice(new BigDecimal(price));
        route.setBusId((short) 101);
        return route;
    }

    private ReservationDto reservationDto(Route route, LocalDateTime departure) {
        ReservationDto dto = new ReservationDto();
        dto.setRouteId(route.getId());
        dto.setSeats(1);
        dto.setTravelDepartureTime(departure);
        return dto;
    }

    private Vehicle vehicle(Short fleetNumber, int seats) {
        Vehicle vehicle = new Vehicle();
        vehicle.setFleetNumber(fleetNumber);
        vehicle.setSeats(seats);
        return vehicle;
    }
}
