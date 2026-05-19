package pasir.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pasir.model.User;
import pasir.model.Wallet;
import pasir.repositories.UserRepository;
import pasir.repositories.WalletRepository;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class PointsService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public User updatePoints(int ammount, String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {return null;}

        int currentPoints = user.getWallet() == null ? 0 : user.getWallet().getPoints();
        int updatedPoints = currentPoints + ammount;

        Wallet wallet = user.getWallet();
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            wallet.setMoney(BigDecimal.ZERO);
            user.setWallet(wallet);
        }

        wallet.setPoints(updatedPoints);
        user.setPoints(updatedPoints);
        walletRepository.save(wallet);
        return userRepository.save(user);
    }

}
