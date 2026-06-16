package pasir.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pasir.model.Vehicle;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByFleetNumber(Short fleetNumber);
}
