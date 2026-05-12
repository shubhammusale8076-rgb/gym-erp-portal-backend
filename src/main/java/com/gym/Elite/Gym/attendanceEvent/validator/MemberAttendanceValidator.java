package com.gym.Elite.Gym.attendanceEvent.validator;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventDto;
import com.gym.Elite.Gym.attendanceEvent.dto.ValidationResult;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.repo.AttendanceRepo;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.entity.MemberSubscription;
import com.gym.Elite.Gym.auth.entity.SubscriptionStatus;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.auth.repo.SubscriptionPlanRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberAttendanceValidator implements AttendanceValidationStrategy {

    private final MemberRepo memberRepo;
    private final SubscriptionPlanRepo subscriptionRepo;
    private final AttendanceRepo attendanceRepo;

    private static final int DUPLICATE_WINDOW_MINUTES = 10;

    @Override
    public ValidationResult validate(UUID tenantId, UUID actorId, AttendanceEventDto event) {
        
        // 1. Member active status
        Optional<Member> memberOpt = memberRepo.findByIdAndTenantId(actorId, tenantId);
        if (memberOpt.isEmpty() || Boolean.FALSE.equals(memberOpt.get().getActive())) {
            return ValidationResult.failure("MEMBER_INACTIVE", "Member account is inactive or not found");
        }

        // 2. Subscription check
        Optional<MemberSubscription> subOpt = subscriptionRepo.findActiveSubscription(tenantId, actorId);
        if (subOpt.isEmpty()) {
            return ValidationResult.failure("SUBSCRIPTION_INVALID", "No active subscription found");
        }

        MemberSubscription sub = subOpt.get();
        if (sub.getStatus() == SubscriptionStatus.FROZEN || sub.getStatus() == SubscriptionStatus.PAUSED) {
            return ValidationResult.failure("SUBSCRIPTION_FROZEN", "Membership is currently frozen");
        }

        // 3. Duplicate session check
        boolean hasActiveSession = attendanceRepo.findFirstByActorIdAndActorTypeAndStatusAndTenantIdOrderByCheckInTimeDesc(
                actorId, AttendanceActorType.MEMBER, com.gym.Elite.Gym.attendanceEvent.enums.AttendanceStatus.CHECKED_IN, tenantId).isPresent();
        
        if (hasActiveSession) {
            return ValidationResult.failure("ALREADY_CHECKED_IN", "Member already has an active session");
        }

        // 4. Duplicate window check
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(DUPLICATE_WINDOW_MINUTES);
        boolean isDuplicate = attendanceRepo.existsRecentCheckIn(actorId, AttendanceActorType.MEMBER, tenantId, windowStart);
        if (isDuplicate) {
            return ValidationResult.failure("DUPLICATE_ATTENDANCE", "Duplicate check-in within window");
        }

        return ValidationResult.success();
    }

    @Override
    public AttendanceActorType getSupportedType() {
        return AttendanceActorType.MEMBER;
    }
}
