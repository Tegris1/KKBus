package pasir.services;

import lombok.AllArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pasir.Mappers.UserMapper;
import pasir.Security.JwtUtil;
import pasir.dtos.LoginDto;
import pasir.dtos.UserDto;
import pasir.exceptions.UserAlreadyExistsException;
import pasir.model.User;
import pasir.repositories.UserRepository;

import java.util.Collections;

@NullMarked
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->  new UsernameNotFoundException("Nie znaleziono Uzytkownika " +  email));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public User register(UserDto dto) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new UserAlreadyExistsException("Email already registered");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

    public String login(LoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Nieprawidłowe dane logowania");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    public User updateUserDetails(UserDto dto, String email) {
        User oldUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));
        User newUser = userMapper.updateUser(dto, oldUser);
        return userRepository.save(newUser);
    }

    public User updateUserDetails(UserDto dto, Long id) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));
        User newUser = userMapper.updateUser(dto, oldUser);
        return userRepository.save(newUser);
    }
}