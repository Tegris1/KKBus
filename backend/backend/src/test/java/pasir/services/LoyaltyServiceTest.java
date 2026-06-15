package pasir.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import pasir.dtos.RewardRedemptionDto;
import pasir.model.Reward;
import pasir.model.RewardRedemption;
import pasir.model.User;
import pasir.model.Wallet;
import pasir.repositories.RewardRedemptionRepository;
import pasir.repositories.RewardRepository;
import pasir.repositories.UserRepository;
import pasir.repositories.WalletRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceTest {
    @Mock
    private RewardRepository rewardRepository;
    @Mock
    private RewardRedemptionRepository redemptionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void redeemDeductsPointsAndCreatesVoucher() {
        User user = new User();
        user.setEmail("user@example.com");
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setPoints(30);
        user.setWallet(wallet);

        Reward reward = new Reward();
        reward.setId(2L);
        reward.setName("Bon na kanapke");
        reward.setDescription("Bon testowy");
        reward.setPrice(25);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null)
        );
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(rewardRepository.findById(reward.getId())).thenReturn(Optional.of(reward));
        when(walletRepository.findByUserForUpdate(user)).thenReturn(Optional.of(wallet));
        when(redemptionRepository.save(any(RewardRedemption.class))).thenAnswer(invocation -> {
            RewardRedemption redemption = invocation.getArgument(0);
            redemption.setId(10L);
            return redemption;
        });

        RewardRedemptionDto result = service().redeem(reward.getId());

        assertEquals(5, wallet.getPoints());
        assertEquals(5, user.getPoints());
        assertEquals("Bon na kanapke", result.rewardName());
        assertNotNull(result.voucherCode());
        verify(walletRepository).save(wallet);
        verify(redemptionRepository).save(any(RewardRedemption.class));
    }

    private LoyaltyService service() {
        return new LoyaltyService(
                rewardRepository,
                redemptionRepository,
                userRepository,
                walletRepository
        );
    }
}
