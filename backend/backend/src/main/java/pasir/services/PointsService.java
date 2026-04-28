package pasir.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pasir.model.User;
import pasir.repositories.UserRepository;

@Service
@AllArgsConstructor
public class PointsService {
    private final UserRepository userRepository;
    public User updatePoints(int ammount, String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {return null;}
        user.setPoints(user.getPoints() + ammount);
        return userRepository.save(user);
    }

}
