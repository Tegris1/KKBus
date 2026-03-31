package pk.jj.pasir_jasek_jakub.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pk.jj.pasir_jasek_jakub.model.TransactionType;

@Getter
@Setter
public class TransactionDto {
    @NotNull(message = "Kwota nie moze byc pusta")
    @Min(value = 1, message = "Kwota nie moze byc mniejsza od 1")
    private Double amount;

    @NotNull(message = "Typ nie moze byc pusty")
    private TransactionType type;

    @Size(max = 50, message = "Tagi mogą mieć maksymalnie 50 znaków")
    private String tags;

    @Size(max = 255, message = "Notatki mogą mieć maksymalnie 255 znaków")
    private String notes;

}
