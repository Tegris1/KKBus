package pk.jj.pasir_jasek_jakub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.jj.pasir_jasek_jakub.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}