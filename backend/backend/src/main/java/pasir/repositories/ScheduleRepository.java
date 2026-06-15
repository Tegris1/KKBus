package pasir.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pasir.model.Schedule;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // Możesz tu dodać dedykowane metody, np. szukanie grafiku dla konkretnego kierowcy:
    List<Schedule> findAllByEmployeeId(Long employeeId);

    boolean existsByEmployeeIdAndBusIdAndWorkingDate(Long employeeId, Short busId, LocalDate workingDate);
}
