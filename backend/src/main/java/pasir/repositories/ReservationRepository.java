package pasir.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pasir.model.Reservation;
import pasir.model.User;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUser(User user);


}