package pasir.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pasir.dtos.ScheduleRequestDTO;
import pasir.dtos.ScheduleResponseDTO;
import pasir.mappers.ScheduleMapper;
import pasir.model.Schedule;
import pasir.repositories.ScheduleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;

    public List<ScheduleResponseDTO> getAll() {
        return scheduleRepository.findAll().stream()
                .map(scheduleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ScheduleResponseDTO getById(Long id) {
        return scheduleRepository.findById(id)
                .map(scheduleMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono grafiku o ID: " + id));
    }

    @Transactional
    public ScheduleResponseDTO create(ScheduleRequestDTO dto) {
        Schedule schedule = scheduleMapper.toEntity(dto);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toResponseDTO(savedSchedule);
    }

    @Transactional
    public ScheduleResponseDTO update(Long id, ScheduleRequestDTO dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brak grafiku o ID: " + id));

        scheduleMapper.updateEntity(schedule, dto);
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toResponseDTO(updatedSchedule);
    }

    @Transactional
    public void delete(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new EntityNotFoundException("Nie można usunąć. Brak grafiku o ID: " + id);
        }
        scheduleRepository.deleteById(id);
    }
}