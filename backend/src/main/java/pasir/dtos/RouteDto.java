package pasir.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter

public class RouteDto {

    @NotNull(message = "Must have route origin")
    private String origin;

    @NotNull(message = "Must have route departure time")
    private LocalDateTime departureTime;

    @NotNull(message = "Must have route destination")
    private String destination;
    @NotNull(message = "Must have route arrival time")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Must have route price")
    @Min(value = 1, message = "Price must be greater than 1")
    private BigDecimal price;
}
