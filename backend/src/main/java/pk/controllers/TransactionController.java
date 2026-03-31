package pk.jj.pasir_jasek_jakub.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pk.jj.pasir_jasek_jakub.dtos.TransactionDto;
import pk.jj.pasir_jasek_jakub.model.Transaction;
import pk.jj.pasir_jasek_jakub.repositories.TransactionRepository;
import pk.jj.pasir_jasek_jakub.services.TransactionService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }



    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(){
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDto transactionDto
            ){
        Transaction changedTransaction = transactionService.updateTransaction(id, transactionDto);
        return ResponseEntity.ok(changedTransaction);
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionDto transactionDto){
        Transaction newTransaction = transactionService.createTransaction(transactionDto);
        return ResponseEntity.ok(newTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id){
        transactionService.deleteTransaction(id);

        return ResponseEntity.ok().build();
    }

}
