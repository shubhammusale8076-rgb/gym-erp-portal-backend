package com.gym.Elite.Gym.attendanceEvent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSessionDTO {

    private String name;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private String location;
    private Integer capacity;

    private String trainerName;
}
