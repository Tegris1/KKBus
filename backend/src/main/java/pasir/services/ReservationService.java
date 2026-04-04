package pasir.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pasir.dtos.ReservationDto;
import pasir.model.Reservation;
import pasir.model.User;
import pasir.repositories.ReservationRepository;
import pasir.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;


    public Reservation getTransactionById(Long id) {
        return reservationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono transakcji o ID" + id)
        );
    }

    public Reservation updateTransaction(Long id, ReservationDto reservationDto) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono transakcji o ID" + id)
        );

        if(!(reservation.getUser().getEmail().equals(getCurrentUser().getEmail()))) {
            throw new AccessDeniedException("Nie masz dostepu do tej transakcji");
        }
        reservation.setAmount(reservationDto.getAmount());
        reservation.setType(reservationDto.getType());
        reservation.setTags(reservationDto.getTags());
        reservation.setNotes(reservationDto.getNotes());

        return reservationRepository.save(reservation);
    }

    public Reservation createTransaction(ReservationDto reservationDto) {
        Reservation reservation = new Reservation();
        reservation.setAmount(reservationDto.getAmount());
        reservation.setType(reservationDto.getType());
        System.out.println(reservationDto.getNotes() + " Notesshdvlksjahkjlhkjhasdkjlfhladskjfhlasdkfhlkasdfhklasdfj");
        reservation.setTags(reservationDto.getTags());
        reservation.setNotes(reservationDto.getNotes());
        reservation.setTimestamp(LocalDateTime.now());
        reservation.setUser(getCurrentUser());
        return reservationRepository.save(reservation);
    }

    public void deleteTransaction(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Nie istnieje: " + id)
        );
        if(!(reservation.getUser().getEmail().equals(getCurrentUser().getEmail()))) {
            throw new AccessDeniedException("Nie masz dostepu");
        }
        reservationRepository.deleteById(id);
    }

    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("authentication: " + authentication);
        if (authentication == null || authentication.getName() == null) {
            System.out.println("Authentication is null --- getCurrentUser()");
            throw new AccessDeniedException("Użytkownik nie jest uwierzytelniony");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono zalogowanego użytkownika: " + email));
    }

    public List<Reservation> getAllTransactions(){
        User user = getCurrentUser();
        return reservationRepository.findAllByUser(user);
    }
}
