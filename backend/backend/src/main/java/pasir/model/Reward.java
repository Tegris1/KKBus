package pasir.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rewards")
@Getter
@Setter
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Reward must have a name")
    private String name;

    private String description;

    @NotNull(message = "Reward must have a price")
    @Min(value = 1, message = "Price must be greater than 1")
    private int price;
}
