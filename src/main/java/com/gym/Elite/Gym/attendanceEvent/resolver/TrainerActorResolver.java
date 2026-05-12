package com.gym.Elite.Gym.attendanceEvent.resolver;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.trainer.entity.Trainer;
import com.gym.Elite.Gym.trainer.repo.TrainerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TrainerActorResolver implements AttendanceActorResolver {

    private final TrainerRepo trainerRepo;

    @Override
    public Optional<ResolvedActor> resolveById(UUID tenantId, UUID id) {
        return trainerRepo.findByIdAndTenantId(id, tenantId)
                .map(this::map);
    }

    @Override
    public Optional<ResolvedActor> resolveByCode(UUID tenantId, String code) {
        return Optional.empty(); // To be expanded
    }

    @Override
    public AttendanceActorType getSupportedType() {
        return AttendanceActorType.TRAINER;
    }

    private ResolvedActor map(Trainer trainer) {
        return ResolvedActor.builder()
                .id(trainer.getId())
                .name(trainer.getFullName())
                .type(AttendanceActorType.TRAINER)
                .active(Boolean.TRUE.equals(trainer.getActive()))
                .build();
    }
}
