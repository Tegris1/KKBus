package pasir.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pasir.model.Route;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select route from Route route where route.id = :id")
    Optional<Route> findByIdForUpdate(@Param("id") Long id);

    List<Route> findByDestinationAndOriginOrderByDepartureTimeDesc(String destination, String origin);

    List<Route> findAllByDepartureTimeGreaterThanEqualAndDepartureTimeLessThanOrderByDepartureTime(
            LocalDateTime periodStart,
            LocalDateTime periodEnd
    );
}
