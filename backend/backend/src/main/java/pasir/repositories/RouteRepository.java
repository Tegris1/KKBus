package pasir.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pasir.model.Route;

import java.time.LocalDateTime;
import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByDestinationAndOrigin(String destination, String origin);

    List<Route> findAllByDepartureTimeGreaterThanEqualAndDepartureTimeLessThanOrderByDepartureTime(
            LocalDateTime periodStart,
            LocalDateTime periodEnd
    );
}
