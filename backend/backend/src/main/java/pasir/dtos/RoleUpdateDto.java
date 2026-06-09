package pasir.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pasir.model.Role;

@Getter
@Setter
public class RoleUpdateDto {
    @NotNull
    private Role role;
}
