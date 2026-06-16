package pasir.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pasir.model.Reservation;
import pasir.model.Route;
import pasir.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUser(User user);

    List<Reservation> findAllByUserAndRouteIsNotNull(User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select reservation from Reservation reservation where reservation.id = :id")
    Optional<Reservation> findByIdForUpdate(@Param("id") Long id);

    List<Reservation> findAllByRouteIn(List<Route> routes);

    @Query("""
            select reservation
            from Reservation reservation
            join fetch reservation.route route
            join fetch reservation.user user
            where route.driverId = :driverId
            order by reservation.travelDepartureTime asc, route.departureTime asc, user.lastName asc, user.username asc
            """)
    List<Reservation> findDriverPassengerReservations(@Param("driverId") Long driverId);

    @Query("""
            select coalesce(sum(coalesce(reservation.seats, 1)), 0)
            from Reservation reservation
            where reservation.route = :route
              and reservation.travelDepartureTime = :travelDepartureTime
            """)
    Long countReservedSeats(
            @Param("route") Route route,
            @Param("travelDepartureTime") LocalDateTime travelDepartureTime
    );

    boolean existsByUserAndRoute(User user, Route route);

}
