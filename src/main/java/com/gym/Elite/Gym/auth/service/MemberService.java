package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceDTO;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.repo.AttendanceRepo;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.memberDto.FinancialDTO;
import com.gym.Elite.Gym.auth.dto.memberDto.MemberDetailResponseDTO;
import com.gym.Elite.Gym.auth.dto.memberDto.MemberRequestDTO;
import com.gym.Elite.Gym.auth.dto.memberDto.MemberResponseDTO;
import com.gym.Elite.Gym.auth.dto.membershipPlanDto.PlanDTO;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.entity.MemberSubscription;
import com.gym.Elite.Gym.auth.entity.MembershipPlan;
import com.gym.Elite.Gym.auth.entity.SubscriptionStatus;
import com.gym.Elite.Gym.auth.mapper.MemberMapper;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.auth.repo.MembershipPlanRepo;
import com.gym.Elite.Gym.auth.repo.SubscriptionPlanRepo;
import com.gym.Elite.Gym.common.security.EncryptionService;
import com.gym.Elite.Gym.integration.client.EventPublisher;
import com.gym.Elite.Gym.payment.dto.TransactionDTO;
import com.gym.Elite.Gym.payment.repo.PaymentRepo;
import com.gym.Elite.Gym.trainer.dto.TrainerMemberDTO;
import com.gym.Elite.Gym.trainer.entity.TrainerMemberAssignment;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepo memberRepo;
    private final MemberMapper memberMapper;
    private final MembershipPlanRepo planRepo;
    private final AttendanceRepo attendanceRepo;
    private final PaymentRepo paymentRepo;
    private final SubscriptionPlanRepo subscriptionRepo;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;
    private final EncryptionService encryptionService;

    public ResponseDto createMember(MemberRequestDTO request) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();
        String email = request.getEmail().trim().toLowerCase();

        String encryptedAadhaar = null;
        if (request.getAadhaarNumber() != null) {
            encryptedAadhaar = encryptionService.encrypt(request.getAadhaarNumber());
        }

        if (memberRepo.existsByEmailAndTenantId(request.getEmail(), tenantId)) {
            throw new RuntimeException("Member already exists");
        }

        MembershipPlan plan = planRepo.findByIdAndTenantId(request.getPlanId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Invalid plan"));

        Member member = Member.builder()
                .fullName(request.getFullName())
                .email(email)
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .aadhaarNumber(encryptedAadhaar) // 🔒 store carefully
                .active(true)
                .profileImageUrl(request.getProfileImageUrl())
                .profileImagePublicId(request.getProfileImagePublicId())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactNumber(request.getEmergencyContactNumber())
                .tenantId(tenantId)
                .build();

        memberRepo.save(member);

        // ✅ Create Subscription (PENDING)
        MemberSubscription subscription = MemberSubscription.builder()
                .member(member)
                .plan(plan)
                .durationInDays(request.getDurationInDays()) // 🔥 from frontend
                .price(calculatePrice(plan, request.getDurationInDays()))
                .status(SubscriptionStatus.PENDING)
                .active(false)
                .autoRenew(false)
                .remainingSessions(plan.getSessionLimit())
                .tenantId(tenantId)
                .build();

        subscriptionRepo.save(subscription);

        // 🚀 Trigger event via EventPublisher
        eventPublisher.publish("MEMBERSHIP_CREATED", tenantId.toString(), member);

        return ResponseDto.builder().code(201).message("Member Created").build();
    }

    public MemberDetailResponseDTO getMemberById(UUID memberId) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Member member = getEntity(memberId,tenantId);

        // 🔥 2. Active Subscription
        MemberSubscription subscription = subscriptionRepo
                .findTopByMemberIdAndTenantIdOrderByCreatedOnDesc(memberId, tenantId)
                .orElse(null);

        Integer attendanceCount = attendanceRepo.countByActorIdAndActorType(memberId, AttendanceActorType.MEMBER);

        String trainerName = member.getTrainerAssignments() != null
                ? member.getTrainerAssignments().stream()
                .filter(a -> Boolean.TRUE.equals(a.getActive()))
                .findFirst()
                .map(a -> a.getTrainer().getFullName())
                .orElse("Self")
                : "Self";

        String programName = member.getTrainerAssignments() != null
                ? member.getTrainerAssignments().stream()
                .filter(a -> Boolean.TRUE.equals(a.getActive()))
                .findFirst()
                .map(TrainerMemberAssignment::getGoal)
                .orElse("Weight Training")
                : "Weight Training";

        List<TransactionDTO> transactions = paymentRepo
                .findTop5ByMemberIdOrderByCreatedOnDesc(memberId)
                .stream()
                .map(p -> {

                    // 🔥 Title logic
                    String title = "Payment";

                    if (p.getItems() != null && !p.getItems().isEmpty()) {
                        title = p.getItems().get(0).getName(); // assuming PaymentItem has name
                    }

                    // 🔥 Better type formatting
                    String type = p.getPaymentMethod() != null
                            ? p.getPaymentMethod().toUpperCase()
                            : "UNKNOWN";

                    return TransactionDTO.builder()
                            .title(title)
                            .date(p.getPaymentDate() != null ? p.getPaymentDate() : p.getCreatedOn())
                            .amount(p.getTotalAmount())
                            .status(p.getStatus().name())
                            .type(type)
                            .build();
                })
                .toList();

        List<AttendanceDTO> attendanceTimeline = attendanceRepo
                .findTop5ByActorIdAndActorTypeOrderByCheckInTimeDesc(
                        memberId,
                        AttendanceActorType.MEMBER
                )
                .stream()
                .map(a -> AttendanceDTO.builder()
                        .dateTime(a.getCheckInTime())
                        .status(a.getStatus().name())
                        .activityName(
                                subscription != null && subscription.getPlan() != null
                                        ? subscription.getPlan().getName()
                                        : "Workout Session"
                        ) // plan name
                        .instructor(trainerName)
                        .location("Gym Floor")// trainer name
                        .build())
                .toList();

        PlanDTO planDto = null;
        if (subscription != null && subscription.getPlan() != null) {
            MembershipPlan plan = subscription.getPlan();

            planDto = PlanDTO.builder()
                    .name(plan.getName())
                    .description("Premium membership plan")
                    .status(plan.getActive().toString())
                    .badge(plan.getBadge())
                    .price(plan.getPrice())
                    .durationInDays(subscription.getDurationInDays())
                    .features(plan.getFeatures())
                    .build();
        }
        FinancialDTO financial = FinancialDTO.builder()
                .nextPaymentDate(
                        subscription != null && subscription.getEndDate() != null
                                ? subscription.getEndDate().toLocalDate()
                                : null
                )
                .amountDue(subscription != null ? subscription.getPrice() : 0.0)
                .build();

        TrainerMemberDTO trainerMemberDTO = TrainerMemberDTO.builder()
                .fullName(trainerName)
                .programName(programName)
                        .build();

        return MemberDetailResponseDTO.builder()
                .memberId(member.getId())
                .memberCode("AUR-" + member.getId().toString().substring(0, 4))
                .fullName(member.getFullName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .profileImageUrl(member.getProfileImageUrl())

                .joinDate(member.getCreatedOn().toLocalDate())
                .membershipExpiry(
                        subscription != null && subscription.getEndDate() != null
                                ? subscription.getEndDate().toLocalDate()
                                : null
                )
                .totalAttendance(attendanceCount)
                .accountBalance(0.0) // 👉 replace with wallet logic later

                .plan(planDto)
                .trainerMemberDTO(trainerMemberDTO)
                .recentTransactions(transactions)
                .attendanceTimeline(attendanceTimeline)
                .financial(financial)

                .build();
    }

    public List<MemberResponseDTO> getMembersByTenant() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

//        if (!tenantId.equals(currentTenantId)) {
//            throw new RuntimeException("Unauthorized: Access denied for this tenant");
//        }

        List<Member> members = memberRepo.findAllWithSubscriptions(tenantId);

        return members.stream()
                .map(memberMapper::mapToDTO)
                .toList();
    }

    public ResponseDto updateMember(UUID memberId, MemberRequestDTO request) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Member member = getEntity(memberId,tenantId);

        UUID currentTenantId = SecurityUtils.getCurrentTenantId();
        if (!currentTenantId.equals(member.getTenantId())) {
            throw new RuntimeException("Unauthorized");
        }

        member.setFullName(request.getFullName());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setEmail(request.getEmail());

        memberRepo.save(member);


        return ResponseDto.builder().code(200).message("Member Updated Successfully").build();
    }

    public ResponseDto deleteMember(UUID memberId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Member member = getEntity(memberId,tenantId);

        UUID currentTenantId = SecurityUtils.getCurrentTenantId();
        if (!currentTenantId.equals(member.getTenantId())) {
            throw new RuntimeException("Unauthorized");
        }

        memberRepo.delete(member);
        return ResponseDto.builder().code(200).message("Member Deleted Successfully").build();
    }

    public ResponseDto activateMember(UUID memberId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Member member = getEntity(memberId,tenantId);

        UUID currentTenantId = SecurityUtils.getCurrentTenantId();
        if (!currentTenantId.equals(member.getTenantId())) {
            throw new RuntimeException("Unauthorized");
        }

        member.setActive(true);
        memberRepo.save(member);
        return ResponseDto.builder().code(200).message("Member Activated Successfully").build();
    }

    public ResponseDto deactivateMember(UUID memberId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Member member = getEntity(memberId,tenantId);

        UUID currentTenantId = SecurityUtils.getCurrentTenantId();
        if (!currentTenantId.equals(member.getTenantId())) {
            throw new RuntimeException("Unauthorized");
        }

        member.setActive(false);
        memberRepo.save(member);
        return ResponseDto.builder().code(200).message("Member De-Activated Successfully").build();
    }

    private Member getEntity(UUID memberId , UUID tenantId) {
        return memberRepo.findByIdAndTenantId(memberId, tenantId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    private Double calculatePrice(MembershipPlan plan, int durationInDays) {
        double monthlyPrice = plan.getPrice();

        int months = durationInDays / 30;

        double total = monthlyPrice * months;

        // Example discount
        if (months >= 6) {
            total *= 0.9; // 10% discount
        }

        return total;
    }
}
