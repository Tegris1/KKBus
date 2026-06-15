package pasir.mappers;
import pasir.model.Schedule;

import pasir.dtos.ScheduleRequestDTO;
import pasir.dtos.ScheduleResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper {

    // Mapowanie z Encji na ResponseDTO
    public ScheduleResponseDTO toResponseDTO(Schedule entity) {
        if (entity == null) return null;

        return ScheduleResponseDTO.builder()
                .scheduleId(entity.getScheduleId())
                .employeeId(entity.getEmployeeId())
                .busId(entity.getBusId())
                .dayOfWeek(resolveDayOfWeek(entity))
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .build();
    }

    // Mapowanie z RequestDTO na nową Encję
    public Schedule toEntity(ScheduleRequestDTO dto) {
        if (dto == null) return null;

        Schedule entity = new Schedule();
        updateEntity(entity, dto);
        return entity;
    }

    // Aktualizacja istniejącej Encji na podstawie RequestDTO
    public void updateEntity(Schedule entity, ScheduleRequestDTO dto) {
        if (dto == null || entity == null) return;

        entity.setEmployeeId(dto.getEmployeeId());
        entity.setBusId(dto.getBusId());
        entity.setDayOfWeek(dto.getDayOfWeek());
        entity.setWorkingDate(null);
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
    }

    private java.time.DayOfWeek resolveDayOfWeek(Schedule entity) {
        if (entity.getDayOfWeek() != null) {
            return entity.getDayOfWeek();
        }

        if (entity.getWorkingDate() != null) {
            return entity.getWorkingDate().getDayOfWeek();
        }

        return null;
    }
}
