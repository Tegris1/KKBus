package pasir.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedules")
@SuppressWarnings("JpaDataSourceInspection")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    private Long employeeId;

    private Short busId;

    private LocalDate workingDate;

    private LocalTime startTime;

    private LocalTime endTime;
}