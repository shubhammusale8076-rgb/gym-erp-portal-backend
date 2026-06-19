package com.gym.Elite.Gym.attendanceEvent.dto;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AttendanceActorSearchDto {

    private UUID id;
    private String name;
    private AttendanceActorType actorType;
    private String phone;
    private Boolean active;
    private String membershipPlan;
}