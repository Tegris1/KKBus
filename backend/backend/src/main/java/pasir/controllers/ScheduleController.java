package pasir.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    /*private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDTO>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ScheduleResponseDTO> createSchedule(
            @RequestBody @Valid ScheduleRequestDTO requestDTO) {

        return ResponseEntity.ok(scheduleService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> updateSchedule(
            @PathVariable Long id,
            @RequestBody @Valid ScheduleRequestDTO requestDTO) {

        return ResponseEntity.ok(scheduleService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }*/
}
