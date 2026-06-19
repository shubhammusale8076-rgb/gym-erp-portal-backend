package com.gym.Elite.Gym.attendanceEvent.service;

import com.gym.Elite.Gym.attendanceEvent.audit.AttendanceAudit;
import com.gym.Elite.Gym.attendanceEvent.audit.AttendanceAuditRepository;
import com.gym.Elite.Gym.attendanceEvent.dto.*;
import com.gym.Elite.Gym.attendanceEvent.entity.Attendance;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceStatus;
import com.gym.Elite.Gym.attendanceEvent.exception.AttendanceException;
import com.gym.Elite.Gym.attendanceEvent.mapper.AttendanceMapper;
import com.gym.Elite.Gym.attendanceEvent.repo.AttendanceRepo;
import com.gym.Elite.Gym.attendanceEvent.resolver.AttendanceActorResolver;
import com.gym.Elite.Gym.attendanceEvent.resolver.ResolverFactory;
import com.gym.Elite.Gym.attendanceEvent.resolver.ResolvedActor;
import com.gym.Elite.Gym.attendanceEvent.validator.AttendanceValidationStrategy;
import com.gym.Elite.Gym.attendanceEvent.validator.ValidationStrategyFactory;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.trainer.entity.Trainer;
import com.gym.Elite.Gym.trainer.repo.TrainerRepo;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Unified Enterprise Attendance Engine.
 *
 * Refactored to support Actor Type Architecture:
 * - Uses Resolvers to find actors (Member, Trainer, Staff).
 * - Uses Validation Strategies for actor-specific business rules.
 * - Centralizes lifecycle management for all attendance events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceRepo attendanceRepo;
    private final AttendanceAuditRepository auditRepo;
    private final MemberRepo memberRepo;
    private final TrainerRepo trainerRepo;
    private final ResolverFactory resolverFactory;
    private final ValidationStrategyFactory validationFactory;

    /**
     * Entry point for device-driven attendance (Biometric, RFID, QR).
     */
    @Transactional
    public AttendanceEventResponseDto recordAttendanceEvent(AttendanceEventDto event) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        
        // 1. Resolve Actor
        ResolvedActor actor = resolveActor(tenantId, event);
        log.info("Processing attendance for {}: {} (ID: {})", actor.getType(), actor.getName(), actor.getId());

        // 2. Determine Action (Check-in or Check-out)
        Optional<Attendance> openSession = attendanceRepo.findFirstByActorIdAndActorTypeAndStatusAndTenantIdOrderByCheckInTimeDesc(
                actor.getId(), actor.getType(), AttendanceStatus.CHECKED_IN, tenantId);

        if (openSession.isPresent()) {
            Attendance attendance =  processCheckOut(openSession.get(), event);

            return AttendanceEventResponseDto.builder()
                    .success(true)
                    .message("Check-out successful")
                    .attendanceId(attendance.getId())
                    .actorId(actor.getId())
                    .actorName(actor.getName())
                    .actorType(actor.getType())
                    .status(attendance.getStatus())
                    .eventType("CHECK_OUT")
                    .timestamp(LocalDateTime.now())
                    .build();
        } else {
            // 3. Validate for Check-in
            AttendanceValidationStrategy validator = validationFactory.getStrategy(actor.getType());
            ValidationResult validation = validator.validate(tenantId, actor.getId(), event);

            if (!validation.isValid()) {
                log.warn("Attendance rejected for {}: {}", actor.getName(), validation.getFailureReason());
                logAudit(tenantId, actor, "REJECTED", null, validation.getFailureReason(), event);

                return AttendanceEventResponseDto.builder()
                        .success(false)
                        .message(validation.getFailureReason())
                        .actorId(actor.getId())
                        .actorName(actor.getName())
                        .actorType(actor.getType())
                        .status(AttendanceStatus.REJECTED)
                        .eventType("REJECTED")
                        .timestamp(LocalDateTime.now())
                        .build();
            }

            Attendance attendance = processCheckIn(tenantId, actor, event);

            return AttendanceEventResponseDto.builder()
                    .success(true)
                    .message("Check-in successful")
                    .attendanceId(attendance.getId())
                    .actorId(actor.getId())
                    .actorName(actor.getName())
                    .actorType(actor.getType())
                    .status(attendance.getStatus())
                    .eventType("CHECK_IN")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    @Transactional
    public List<AttendanceActorSearchDto> searchActors(String query) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        List<AttendanceActorSearchDto> results = new ArrayList<>();

        log.info("Searching attendance actors. TenantId={}, Query={}",
                tenantId, query);

        List<Member> members = memberRepo.searchAttendanceMembers(tenantId, query);

        log.info("Members found={}", members.size());

        List<Trainer> trainers = trainerRepo.searchAttendanceTrainers(tenantId, query);

        log.info("Trainers found={}", trainers.size());

        members.forEach(member ->
                results.add(
                        AttendanceActorSearchDto.builder()
                                .id(member.getId())
                                .name(member.getFullName())
                                .actorType(AttendanceActorType.MEMBER)
                                .phone(member.getPhoneNumber())
                                .active(member.getActive())
                                .membershipPlan(getMembershipPlan(member))
                                .build()
                ));

        trainers.forEach(trainer ->
                results.add(
                        AttendanceActorSearchDto.builder()
                                .id(trainer.getId())
                                .name(trainer.getFullName())
                                .actorType(AttendanceActorType.TRAINER)
                                .phone(trainer.getPhoneNumber())
                                .active(trainer.getActive())
                                .membershipPlan("Trainer")
                                .build()
                ));

        return results;
    }

    /**
     * Manual check-in via dashboard.
     */
//    @Transactional
//    public AttendanceResponse manualCheckIn(ManualAttendanceRequest request) {
//        UUID tenantId = SecurityUtils.getCurrentTenantId();
//
//        AttendanceActorResolver resolver = resolverFactory.getResolver(request.getActorType());
//        ResolvedActor actor = resolver.resolveById(tenantId, request.getActorId())
//                .orElseThrow(() -> new AttendanceException("Actor not found: " + request.getActorId()));
//
//        // Validate
//        AttendanceValidationStrategy validator = validationFactory.getStrategy(request.getActorType());
//
//        AttendanceEventDto eventDto = AttendanceEventDto.builder()
//                .actorId(request.getActorId())
//                .actorType(request.getActorType())
//                .source(request.getSource())
//                .notes(request.getNotes())
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        ValidationResult validation = validator.validate(tenantId, actor.getId(), eventDto);
//        if (!validation.isValid()) {
//            throw new AttendanceException(validation.getFailureReason());
//        }
//
//        Attendance attendance = processCheckIn(tenantId, actor, eventDto);
//        return attendanceMapper.toResponse(attendance, actor.getName());
//    }

    private ResolvedActor resolveActor(UUID tenantId, AttendanceEventDto event) {
        AttendanceActorResolver resolver = resolverFactory.getResolver(event.getActorType());
        
        if (event.getActorId() != null) {
            return resolver.resolveById(tenantId, event.getActorId())
                    .orElseThrow(() -> new AttendanceException("Actor not found by ID"));
        } else if (event.getActorCode() != null) {
            return resolver.resolveByCode(tenantId, event.getActorCode())
                    .orElseThrow(() -> new AttendanceException("Actor not found by Code: " + event.getActorCode()));
        }
        
        throw new AttendanceException("No actor identification provided in event");
    }

    private Attendance processCheckIn(UUID tenantId, ResolvedActor actor, AttendanceEventDto event) {
        Attendance attendance = Attendance.builder()
                .tenantId(tenantId)
                .actorId(actor.getId())
                .actorType(actor.getType())
                .attendanceDate(LocalDate.now())
                .checkInTime(event.getTimestamp() != null ? event.getTimestamp() : LocalDateTime.now())
                .status(AttendanceStatus.CHECKED_IN)
                .source(event.getSource())
                .deviceId(event.getDeviceId())
                .verificationId(event.getVerificationId())
                .notes(event.getNotes())
                .verified(true)
                .build();

        Attendance saved = attendanceRepo.save(attendance);
        logAudit(tenantId, actor, "CHECK_IN", saved.getId(), "Check-in successful", event);
        return saved;
    }

    private Attendance processCheckOut(Attendance attendance, AttendanceEventDto event) {
        LocalDateTime checkOutTime = event.getTimestamp() != null ? event.getTimestamp() : LocalDateTime.now();
        attendance.setCheckOutTime(checkOutTime);
        attendance.setStatus(AttendanceStatus.COMPLETED);
        
        long duration = Duration.between(attendance.getCheckInTime(), checkOutTime).toMinutes();
        attendance.setTotalDurationMinutes((int) duration);

        Attendance saved =  attendanceRepo.save(attendance);
        
        ResolvedActor actor = ResolvedActor.builder()
                .id(attendance.getActorId())
                .type(attendance.getActorType())
                .build(); // Name not strictly needed for audit logging here
        
        logAudit(attendance.getTenantId(), actor, "CHECK_OUT", attendance.getId(), "Check-out successful", event);
        return saved;
    }

    private void logAudit(UUID tenantId, ResolvedActor actor, String action, UUID attendanceId, String reason, AttendanceEventDto event) {
        AttendanceAudit audit = AttendanceAudit.builder()
                .tenantId(tenantId)
                .actorId(actor.getId())
                .actorType(actor.getType())
                .attendanceId(attendanceId)
                .action(action)
                .reason(reason)
                .source(event.getSource())
                .modifiedBy("SYSTEM")
                .build();
        auditRepo.save(audit);
    }

    private String getMembershipPlan(Member member) {

        if (member.getSubscriptions() == null || member.getSubscriptions().isEmpty()) {
            return "No Active Plan";
        }

        return member.getSubscriptions()
                .stream()
                .filter(sub ->
                        Boolean.TRUE.equals(sub.getActive()))
                .filter(sub ->
                        sub.getPlan() != null)
                .map(sub ->
                        sub.getPlan().getName())
                .findFirst()
                .orElse("No Active Plan");
    }
}
