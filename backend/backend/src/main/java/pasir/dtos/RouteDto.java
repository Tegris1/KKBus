package pasir.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Size(max = 20, message = "Route can have at most 20 intermediate stops")
    private List<@NotBlank(message = "Intermediate stop cannot be empty") String> intermediateStops = new ArrayList<>();

    @NotNull(message = "Must have route price")
    @Min(value = 1, message = "Price must be greater than 1")
    private BigDecimal price;
}
