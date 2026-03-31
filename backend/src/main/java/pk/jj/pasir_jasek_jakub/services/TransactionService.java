package pk.jj.pasir_jasek_jakub.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pk.jj.pasir_jasek_jakub.dtos.TransactionDto;
import pk.jj.pasir_jasek_jakub.model.Transaction;
import pk.jj.pasir_jasek_jakub.repositories.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono transakcji o ID" + id)
        );
    }

    public Transaction updateTransaction(Long id, TransactionDto transactionDto) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono transakcji o ID" + id)
        );
        transaction.setAmount(transactionDto.getAmount());
        transaction.setType(transactionDto.getType());
        transaction.setTags(transactionDto.getTags());
        transaction.setNotes(transactionDto.getNotes());

        return transactionRepository.save(transaction);
    }

    public Transaction createTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setType(transactionDto.getType());
        System.out.println(transactionDto.getNotes() + " Notes");
        transaction.setTags(transactionDto.getTags());
        transaction.setNotes(transactionDto.getNotes());
        transaction.setTimestamp(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
