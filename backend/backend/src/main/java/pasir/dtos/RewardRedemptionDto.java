package pasir.dtos;

import java.time.LocalDateTime;

public record RewardRedemptionDto(
        Long id,
        String rewardName,
        String description,
        int pointsCost,
        LocalDateTime redeemedAt,
        String voucherCode
) {
}
