package pasir.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pasir.model.Reward;
import pasir.repositories.RewardRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RewardCatalogInitializer implements ApplicationRunner {
    private static final List<RewardDefinition> REWARDS = List.of(
            new RewardDefinition("Bon na kanapke", "Bon do wykorzystania na kanapke w punkcie partnerskim.", 25),
            new RewardDefinition("Darmowy napoj", "Bon na dowolny napoj podczas podrozy.", 15),
            new RewardDefinition("Znizka 10 PLN", "Bon obnizajacy cene kolejnego biletu o 10 PLN.", 50),
            new RewardDefinition("Darmowy bilet", "Bon na darmowy bilet na dowolny kurs krajowy.", 150)
    );

    private final RewardRepository rewardRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (RewardDefinition definition : REWARDS) {
            Reward reward = rewardRepository.findByName(definition.name()).orElseGet(Reward::new);
            reward.setName(definition.name());
            reward.setDescription(definition.description());
            reward.setPrice(definition.pointsCost());
            rewardRepository.save(reward);
        }
    }

    private record RewardDefinition(String name, String description, int pointsCost) {
    }
}
