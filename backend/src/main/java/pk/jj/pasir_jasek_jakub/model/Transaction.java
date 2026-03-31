package pk.jj.pasir_jasek_jakub.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")

@SuppressWarnings("JpaDataSourceDRMInspecion")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private String tags;
    private String notes;
    private LocalDateTime timestamp;
}
