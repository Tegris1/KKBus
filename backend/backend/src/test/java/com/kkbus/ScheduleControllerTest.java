package com.kkbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pasir.dtos.ScheduleRequestDTO;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser // Symuluje zalogowanego użytkownika, jeśli masz Spring Security
    void shouldCreateSchedule() throws Exception {
        // Given (Przygotowanie danych)
        ScheduleRequestDTO request = ScheduleRequestDTO.builder()
                .employeeId(1L)
                .busId((short) 101)
                .dayOfWeek(DayOfWeek.FRIDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .build();

        // When & Then (Wykonanie i sprawdzenie)
        mockMvc.perform(post("/api/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1L))
                .andExpect(jsonPath("$.busId").value(101))
                .andExpect(jsonPath("$.dayOfWeek").value("FRIDAY"));
    }

    @Test
    @WithMockUser
    void shouldGetAllSchedules() throws Exception {
        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenScheduleNotFound() throws Exception {
        mockMvc.perform(get("/api/schedules/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldDeleteSchedule() throws Exception {
        // Najpierw tworzymy, żeby mieć co usuwać (lub załóż, że ID 1 istnieje)
        mockMvc.perform(delete("/api/schedules/1"))
                .andExpect(status().isNoContent());
    }
}
