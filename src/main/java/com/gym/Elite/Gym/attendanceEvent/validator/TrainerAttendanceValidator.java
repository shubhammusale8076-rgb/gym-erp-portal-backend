package com.gym.Elite.Gym.attendanceEvent.validator;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventDto;
import com.gym.Elite.Gym.attendanceEvent.dto.ValidationResult;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.repo.AttendanceRepo;
import com.gym.Elite.Gym.trainer.entity.Trainer;
import com.gym.Elite.Gym.trainer.repo.TrainerLeaveRepository;
import com.gym.Elite.Gym.trainer.repo.TrainerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TrainerAttendanceValidator implements AttendanceValidationStrategy {

    private final TrainerRepo trainerRepo;
    private final TrainerLeaveRepository leaveRepo;
    private final AttendanceRepo attendanceRepo;

    @Override
    public ValidationResult validate(UUID tenantId, UUID actorId, AttendanceEventDto event) {
        
        // 1. Trainer active status
        Optional<Trainer> trainerOpt = trainerRepo.findByIdAndTenantId(actorId, tenantId);
        if (trainerOpt.isEmpty() || Boolean.FALSE.equals(trainerOpt.get().getActive())) {
            return ValidationResult.failure("TRAINER_INACTIVE", "Trainer account is inactive or not found");
        }

        // 2. Leave check
        if (!leaveRepo.findApprovedLeaveForDate(actorId, tenantId, LocalDate.now()).isEmpty()) {
            return ValidationResult.failure("TRAINER_ON_LEAVE", "Trainer is on approved leave today");
        }

        // 3. Duplicate session check
        boolean hasActiveSession = attendanceRepo.findFirstByActorIdAndActorTypeAndStatusAndTenantIdOrderByCheckInTimeDesc(
                actorId, AttendanceActorType.TRAINER, com.gym.Elite.Gym.attendanceEvent.enums.AttendanceStatus.CHECKED_IN, tenantId).isPresent();
        
        if (hasActiveSession) {
            return ValidationResult.failure("ALREADY_CHECKED_IN", "Trainer already has an active session");
        }

        return ValidationResult.success();
    }

    @Override
    public AttendanceActorType getSupportedType() {
        return AttendanceActorType.TRAINER;
    }
}
