package pasir.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pasir.dtos.RewardDto;
import pasir.dtos.RewardRedemptionDto;
import pasir.model.Reward;
import pasir.model.RewardRedemption;
import pasir.model.User;
import pasir.model.Wallet;
import pasir.repositories.RewardRedemptionRepository;
import pasir.repositories.RewardRepository;
import pasir.repositories.UserRepository;
import pasir.repositories.WalletRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoyaltyService {
    private final RewardRepository rewardRepository;
    private final RewardRedemptionRepository redemptionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Transactional(readOnly = true)
    public List<RewardDto> getRewards() {
        User user = getCurrentUser();
        int points = user.getWallet() == null ? 0 : user.getWallet().getPoints();
        return rewardRepository.findAll().stream()
                .map(reward -> toDto(reward, points))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RewardRedemptionDto> getRedemptions() {
        return redemptionRepository.findAllByUserOrderByRedeemedAtDesc(getCurrentUser()).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public RewardRedemptionDto redeem(Long rewardId) {
        User user = getCurrentUser();
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono nagrody o ID " + rewardId));
        Wallet wallet = walletRepository.findByUserForUpdate(user)
                .orElseGet(() -> createWallet(user));
        int currentPoints = wallet.getPoints() == null ? 0 : wallet.getPoints();

        if (currentPoints < reward.getPrice()) {
            throw new IllegalArgumentException("Brak wystarczajacej liczby punktow");
        }

        int remainingPoints = currentPoints - reward.getPrice();
        wallet.setPoints(remainingPoints);
        user.setPoints(remainingPoints);
        walletRepository.save(wallet);

        RewardRedemption redemption = new RewardRedemption();
        redemption.setUser(user);
        redemption.setReward(reward);
        redemption.setRedeemedAt(LocalDateTime.now());
        redemption.setVoucherCode(createVoucherCode());
        return toDto(redemptionRepository.save(redemption));
    }

    private Wallet createWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setMoney(BigDecimal.ZERO);
        wallet.setPoints(user.getPoints() == null ? 0 : user.getPoints());
        user.setWallet(wallet);
        return walletRepository.save(wallet);
    }

    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException("Uzytkownik nie jest uwierzytelniony");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono zalogowanego uzytkownika"));
    }

    private RewardDto toDto(Reward reward, int points) {
        return new RewardDto(
                reward.getId(),
                reward.getName(),
                reward.getDescription(),
                reward.getPrice(),
                points >= reward.getPrice()
        );
    }

    private RewardRedemptionDto toDto(RewardRedemption redemption) {
        Reward reward = redemption.getReward();
        return new RewardRedemptionDto(
                redemption.getId(),
                reward.getName(),
                reward.getDescription(),
                reward.getPrice(),
                redemption.getRedeemedAt(),
                redemption.getVoucherCode()
        );
    }

    private String createVoucherCode() {
        return "KKBUS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
