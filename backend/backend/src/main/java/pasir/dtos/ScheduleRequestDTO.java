package pasir.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequestDTO {

    @NotNull
    private Long employeeId;

    @NotNull
    private Short busId;

    @NotNull
    private LocalDate workingDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;
}