package pasir.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pasir.model.Reward;

import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    Optional<Reward> findByName(String name);
}
