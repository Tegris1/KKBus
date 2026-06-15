package pasir.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations")

@SuppressWarnings("JpaDataSourceDRMInspecion")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private String tags;
    private String notes;
    private LocalDateTime timestamp;
    private Integer seats;
    private Integer awardedPoints = 0;
    private Integer pointsSpent = 0;
    @Column(precision = 19, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private LocalDateTime travelDepartureTime;
    private LocalDateTime travelArrivalTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
}
