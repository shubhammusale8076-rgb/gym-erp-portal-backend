package com.gym.Elite.Gym.attendanceEvent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSessionDTO {

    private String name;
    private LocalDate SessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer capacity;


}
