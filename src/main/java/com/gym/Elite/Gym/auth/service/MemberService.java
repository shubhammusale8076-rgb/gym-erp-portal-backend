package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceDTO;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.repo.AttendanceRepo;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.memberDto.*;
import com.gym.Elite.Gym.auth.dto.membershipPlanDto.PlanDTO;
import com.gym.Elite.Gym.auth.entity.*;
import com.gym.Elite.Gym.auth.repo.*;
import com.gym.Elite.Gym.auth.mapper.MemberMapper;
import com.gym.Elite.Gym.common.security.EncryptionService;
import com.gym.Elite.Gym.integration.client.EventPublisher;
import com.gym.Elite.Gym.integration.dto.PaymentLinkResponse;
import com.gym.Elite.Gym.integration.entity.IntegrationType;
import com.gym.Elite.Gym.payment.dto.TransactionDTO;
import com.gym.Elite.Gym.payment.entity.PaymentTransaction;
import com.gym.Elite.Gym.payment.repo.PaymentRepo;
import com.gym.Elite.Gym.payment.service.PaymentTransactionService;
import com.gym.Elite.Gym.trainer.dto.TrainerMemberDTO;
import com.gym.Elite.Gym.trainer.entity.TrainerMemberAssignment;
import com.gym.Elite.Gym.utility.PasswordGenerator;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
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
    private final GymUserService gymUserService;
    private final GymUserRepo gymUserRepo;
    private final EncryptionService encryptionService;
    private final PaymentTransactionService paymentTransactionService;
    private final RoleRepo roleRepo;

    public MemberCreationResponseDto createMember(MemberRequestDTO request) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();
        String email = request.getEmail().trim().toLowerCase();

        String encryptedAadhaar = null;
        if (request.getAadhaarNumber() != null) {
            encryptedAadhaar = encryptionService.encrypt(request.getAadhaarNumber());
        }

        if (memberRepo.existsByEmailAndTenantId(request.getEmail(), tenantId)) {
            throw new RuntimeException("Member already exists");
        }

        if (gymUserRepo.existsByEmailAndTenantId(email, tenantId)) {
            throw new RuntimeException("A login account already exists for this email");
        }

        MembershipPlan plan = planRepo.findByIdAndTenantId(request.getPlanId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Invalid plan"));

        String generatedPassword = PasswordGenerator.generateStrongPassword();

        GymUser gymUser = gymUserService.createGymUser(
                email,
                generatedPassword,
                tenantId,
                "MEMBER",
                request.getFullName(),
                request.getPhoneNumber(),
                false
        );

        Member member = Member.builder()
                .fullName(request.getFullName())
                .email(email)
                .phoneNumber(request.getPhoneNumber())
                .gymUser(gymUser)
                .aadhaarNumber(encryptedAadhaar) // 🔒 store carefully
                .address(request.getAddress())
                .active(false) // 💳 disabled on creation until onboarding payment is complete
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

        // 🚀 Create PaymentTransaction for Member Onboarding
        PaymentTransaction transaction = paymentTransactionService.createPaymentTransaction(member, subscription);

        // 🚀 Attempt to sync with integration service for Razorpay link generation
        // Remote failures MUST NOT roll back local db transaction, so errors are self-contained
        PaymentLinkResponse paymentResponse = paymentTransactionService.syncPaymentTransaction(transaction, member, subscription);

        // 🚀 Trigger event via EventPublisher

        return MemberCreationResponseDto.builder()
                .memberId(member.getId())
                .fullName(member.getFullName())
                .userName(member.getFullName())
                .role(member.getGymUser().getRole().getRoleCode())
                .temporaryPassword(generatedPassword)
                .paymentLink(paymentResponse.getUniversalPaymentLink())
                .razorpayPaymentLinkId(paymentResponse.getRazorpayOrderId())
                .build();
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
                    .id(plan.getId())
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
                .status(member.getActive().toString())
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

        GymUser gymUser = member.getGymUser();

        UUID currentTenantId = SecurityUtils.getCurrentTenantId();

        if (!currentTenantId.equals(member.getTenantId())) {
            throw new RuntimeException("Unauthorized");
        }

        memberRepo.delete(member);

        gymUserRepo.delete(gymUser);

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
        if (member.getGymUser() != null) {
            member.getGymUser().setEnabled(true);
        }
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
        if (member.getGymUser() != null) {
            member.getGymUser().setEnabled(false);
        }
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

    public List<UserSearchResponseDto> searchUsers(String query) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        List<GymUser> users = gymUserRepo.searchUsers(query, tenantId);

        return users.stream()
                .map(user ->
                        UserSearchResponseDto.builder()
                                .id(user.getId())
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .build()
                )
                .toList();

    }

    public ResponseDto assignRoleToUsers(AssignUsersRoleRequestDto request) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();


        Role role = roleRepo.findRoleForAssignment(request.getRoleId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Role not found."));



        List<GymUser> users = gymUserRepo.findUsersForRoleAssignment(request.getUserIds(), tenantId);

        if (users.isEmpty()) {
            throw new RuntimeException("No users found for assignment."
           );
        }

        users.forEach(user -> user.setRole(role));

        gymUserRepo.saveAll(users);

        return ResponseDto.builder()
                .code(200)
                .message("Role assigned successfully.")
                .build();
    }
}
