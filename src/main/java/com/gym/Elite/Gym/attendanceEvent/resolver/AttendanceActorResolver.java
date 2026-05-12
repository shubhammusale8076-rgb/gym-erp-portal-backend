package com.gym.Elite.Gym.attendanceEvent.resolver;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface to resolve any actor type into a normalized ResolvedActor object.
 * Used for Member, Trainer, Staff lookups.
 */
public interface AttendanceActorResolver {
    
    Optional<ResolvedActor> resolveById(UUID tenantId, UUID id);
    
    Optional<ResolvedActor> resolveByCode(UUID tenantId, String code);
    
    AttendanceActorType getSupportedType();
}
