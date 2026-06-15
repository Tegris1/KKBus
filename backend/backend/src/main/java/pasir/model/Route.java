package pasir.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "route_intermediate_stops", joinColumns = @JoinColumn(name = "route_id"))
    @OrderColumn(name = "stop_order")
    @Column(name = "stop_name")
    private List<String> intermediateStops = new ArrayList<>();

    private BigDecimal price;
    private Long driverId;
    private Short busId;

    @Column(precision = 19, scale = 2)
    private BigDecimal fuelCost = BigDecimal.ZERO;


}
