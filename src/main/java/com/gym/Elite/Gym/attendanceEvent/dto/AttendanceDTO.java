package com.gym.Elite.Gym.attendanceEvent.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {

    private LocalDateTime dateTime;
    private String status;
    private String activityName;
    private String instructor;
    private String location;
}
