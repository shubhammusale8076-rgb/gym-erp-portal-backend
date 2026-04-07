package com.gym.Elite.Gym.attendanceEvent.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class HeatmapResponseDTO {

    private LocalDate date;
    private Integer count; // 0 or 1 (or more if multi-session)
}