package pasir.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pasir.model.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
}