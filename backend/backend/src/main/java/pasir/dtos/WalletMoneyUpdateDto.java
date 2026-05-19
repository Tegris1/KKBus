package pasir.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WalletMoneyUpdateDto {

    @NotNull(message = "Kwota nie moze byc pusta")
    @DecimalMin(value = "0.00", message = "Kwota nie moze byc ujemna")
    private BigDecimal money;
}
