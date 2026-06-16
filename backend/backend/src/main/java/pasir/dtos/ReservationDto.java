package pasir.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pasir.model.TicketDiscountType;
import pasir.model.TransactionType;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationDto {
    private Long routeId;

    @Min(value = 1, message = "Liczba miejsc nie moze byc mniejsza od 1")
    private Integer seats;
    private LocalDateTime travelDepartureTime;
    private Boolean usePointsDiscount = false;
    private Long passengerUserId;
    private String boardingStop;
    private String alightingStop;
    private TicketDiscountType discountType = TicketDiscountType.NONE;

    @Min(value = 1, message = "Kwota nie moze byc mniejsza od 1")
    private Double amount;

    private TransactionType type;

    @Size(max = 50, message = "Tagi mogą mieć maksymalnie 50 znaków")
    private String tags;

    @Size(max = 255, message = "Notatki mogą mieć maksymalnie 255 znaków")
    private String notes;

}
