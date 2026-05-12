package com.gym.Elite.Gym.attendanceEvent.resolver;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberActorResolver implements AttendanceActorResolver {

    private final MemberRepo memberRepo;

    @Override
    public Optional<ResolvedActor> resolveById(UUID tenantId, UUID id) {
        return memberRepo.findByIdAndTenantId(id, tenantId)
                .map(this::map);
    }

    @Override
    public Optional<ResolvedActor> resolveByCode(UUID tenantId, String code) {
        // Members currently use email or aadhaar as code.
        // In a real device setup, they would have a dedicated numeric code.
        // For now, we'll check by email/aadhaar if needed, but usually 
        // ID is passed by mobile/dashboard.
        return Optional.empty(); // To be expanded as per specific hardware needs
    }

    @Override
    public AttendanceActorType getSupportedType() {
        return AttendanceActorType.MEMBER;
    }

    private ResolvedActor map(Member member) {
        return ResolvedActor.builder()
                .id(member.getId())
                .name(member.getFullName())
                .type(AttendanceActorType.MEMBER)
                .active(Boolean.TRUE.equals(member.getActive()))
                .build();
    }
}
