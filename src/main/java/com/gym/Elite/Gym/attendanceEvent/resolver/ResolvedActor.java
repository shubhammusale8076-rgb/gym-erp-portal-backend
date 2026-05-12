package com.gym.Elite.Gym.attendanceEvent.resolver;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ResolvedActor {
    private UUID id;
    private String name;
    private String code;
    private AttendanceActorType type;
    private boolean active;
}
