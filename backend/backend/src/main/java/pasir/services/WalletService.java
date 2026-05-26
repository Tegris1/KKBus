package pasir.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pasir.dtos.WalletDto;
import pasir.model.User;
import pasir.model.Wallet;
import pasir.repositories.UserRepository;
import pasir.repositories.WalletRepository;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletDto getCurrentUserWallet() {
        return toDto(getOrCreateWallet(getCurrentUser()));
    }

    public WalletDto updateMoney(BigDecimal money) {
        Wallet wallet = getOrCreateWallet(getCurrentUser());
        wallet.setMoney(money);
        return toDto(walletRepository.save(wallet));
    }

    public WalletDto updatePoints(Integer points) {
        User user = getCurrentUser();
        Wallet wallet = getOrCreateWallet(user);
        wallet.setPoints(points);
        user.setPoints(points);
        return toDto(walletRepository.save(wallet));
    }

    private Wallet getOrCreateWallet(User user) {
        if (user.getWallet() != null) {
            return user.getWallet();
        }

        return walletRepository.findByUser(user)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUser(user);
                    wallet.setPoints(user.getPoints() == null ? 0 : user.getPoints());
                    wallet.setMoney(BigDecimal.ZERO);
                    user.setWallet(wallet);
                    return walletRepository.save(wallet);
                });
    }

    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException("Uzytkownik nie jest uwierzytelniony");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono zalogowanego uzytkownika: " + email));
    }

    private WalletDto toDto(Wallet wallet) {
        return new WalletDto(
                wallet.getId(),
                wallet.getUser().getId(),
                wallet.getMoney(),
                wallet.getPoints()
        );
    }
}
