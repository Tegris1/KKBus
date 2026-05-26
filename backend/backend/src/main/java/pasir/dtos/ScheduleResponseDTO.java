package pasir.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponseDTO {

    private Long scheduleId;
    private Long employeeId;
    private Short busId;
    private LocalDate workingDate;
    private LocalTime startTime;
    private LocalTime endTime;
}