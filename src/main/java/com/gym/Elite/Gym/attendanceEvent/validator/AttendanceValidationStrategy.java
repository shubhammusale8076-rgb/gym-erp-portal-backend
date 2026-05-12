package com.gym.Elite.Gym.attendanceEvent.validator;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventDto;
import com.gym.Elite.Gym.attendanceEvent.dto.ValidationResult;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;

import java.util.UUID;

/**
 * Strategy interface for actor-specific attendance validation rules.
 */
public interface AttendanceValidationStrategy {

    ValidationResult validate(UUID tenantId, UUID actorId, AttendanceEventDto event);

    AttendanceActorType getSupportedType();
}
