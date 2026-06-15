package pasir.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pasir.dtos.RewardDto;
import pasir.dtos.RewardRedemptionDto;
import pasir.services.LoyaltyService;

import java.util.List;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {
    private final LoyaltyService loyaltyService;

    @GetMapping("/rewards")
    public ResponseEntity<List<RewardDto>> getRewards() {
        return ResponseEntity.ok(loyaltyService.getRewards());
    }

    @GetMapping("/redemptions")
    public ResponseEntity<List<RewardRedemptionDto>> getRedemptions() {
        return ResponseEntity.ok(loyaltyService.getRedemptions());
    }

    @PostMapping("/rewards/{rewardId}/redeem")
    public ResponseEntity<RewardRedemptionDto> redeem(@PathVariable Long rewardId) {
        return ResponseEntity.ok(loyaltyService.redeem(rewardId));
    }
}
