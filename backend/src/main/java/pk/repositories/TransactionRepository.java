package pk.jj.pasir_jasek_jakub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.jj.pasir_jasek_jakub.model.Transaction;
import pk.jj.pasir_jasek_jakub.model.User;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUser(User user);


}