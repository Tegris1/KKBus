package pasir.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "routes")
@Getter
@Setter

public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String origin;
    private LocalDateTime departureTime;

    private String destination;
    private LocalDateTime arrivalTime;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

}
