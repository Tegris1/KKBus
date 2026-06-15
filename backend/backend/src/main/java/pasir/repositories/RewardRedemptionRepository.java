package pasir.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pasir.model.RewardRedemption;
import pasir.model.User;

import java.util.List;

public interface RewardRedemptionRepository extends JpaRepository<RewardRedemption, Long> {
    List<RewardRedemption> findAllByUserOrderByRedeemedAtDesc(User user);
}
