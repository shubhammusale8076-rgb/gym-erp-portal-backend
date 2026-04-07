package com.gym.Elite.Gym.attendanceEvent.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TodaySessionDTO {

    private UUID sessionId;
    private String name;

    private String time;       // "10:00 AM"
    private String location;

    private String status;     // UPCOMING / ONGOING
    private String tag;        // "IN 45M"

    private Integer joinRate;  // %
}