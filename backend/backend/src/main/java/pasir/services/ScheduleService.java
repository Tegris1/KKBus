package pasir.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pasir.dtos.ScheduleRequestDTO;
import pasir.dtos.ScheduleResponseDTO;
import pasir.mappers.ScheduleMapper;
import pasir.model.Role;
import pasir.model.Schedule;
import pasir.model.User;
import pasir.repositories.ScheduleRepository;
import pasir.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final UserRepository userRepository;

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

    public List<ScheduleResponseDTO> getCurrentEmployeeSchedule() {
        User employee = getCurrentUser();
        return scheduleRepository.findAllByEmployeeId(employee.getId()).stream()
                .map(scheduleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleResponseDTO create(ScheduleRequestDTO dto) {
        requireEmployee(dto.getEmployeeId());
        Schedule schedule = scheduleMapper.toEntity(dto);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toResponseDTO(savedSchedule);
    }

    @Transactional
    public ScheduleResponseDTO update(Long id, ScheduleRequestDTO dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brak grafiku o ID: " + id));

        requireEmployee(dto.getEmployeeId());
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

    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException("UĹĽytkownik nie jest uwierzytelniony");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono zalogowanego uĹĽytkownika: " + authentication.getName()));
    }

    private void requireEmployee(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono pracownika o ID: " + employeeId));

        if (employee.getRole() != Role.EMPLOYEE) {
            throw new AccessDeniedException("Grafik moĹĽna utworzyÄ‡ tylko dla pracownika");
        }
    }
}
