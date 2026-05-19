package pasir.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletPointsUpdateDto {

    @NotNull(message = "Punkty nie moga byc puste")
    @Min(value = 0, message = "Punkty nie moga byc ujemne")
    private Integer points;
}
