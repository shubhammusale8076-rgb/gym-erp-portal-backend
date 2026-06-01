package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.memberDto.MemberDto;
import com.gym.Elite.Gym.auth.dto.subscriptionDto.*;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.entity.MemberSubscription;
import com.gym.Elite.Gym.auth.entity.MembershipPlan;
import com.gym.Elite.Gym.auth.entity.SubscriptionStatus;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.auth.repo.MembershipPlanRepo;
import com.gym.Elite.Gym.auth.repo.SubscriptionPlanRepo;
import com.gym.Elite.Gym.payment.dto.PaymentMethodDto;
import com.gym.Elite.Gym.payment.entity.Payment;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final MemberRepo memberRepo;
    private final MembershipPlanRepo membershipPlanRepo;
    private final SubscriptionPlanRepo subscriptionPlanRepo;

    public ResponseDto createSubscription(SubscriptionRequestDTO request) {

        Member member = memberRepo.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        MembershipPlan plan = membershipPlanRepo.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(startDate, plan.getDurationInDays());

        MemberSubscription subscription = MemberSubscription.builder()
                .member(member)
                .plan(plan)
                .startDate(startDate)
                .endDate(endDate)
                .active(true)
                .autoRenew(request.getAutoRenew())
                .remainingSessions(plan.getSessionLimit())
                .status(SubscriptionStatus.ACTIVE)
                .build();

        subscriptionPlanRepo.save(subscription);
        return ResponseDto.builder().code(201).message("Member has successfully subscribed to plan").build();
    }

    public ResponseDto createSubscriptionFromPayment(UUID memberId, UUID planId, Payment payment) {

        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        MembershipPlan plan = membershipPlanRepo.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(startDate, plan.getDurationInDays());

        MemberSubscription sub = MemberSubscription.builder()
                .member(member)
                .plan(plan)
                .startDate(startDate)
                .endDate(endDate)
                .status(SubscriptionStatus.ACTIVE)
                .active(true)
                .autoRenew(false)
                .remainingSessions(plan.getSessionLimit())
                .payment(payment)
                .build();

        subscriptionPlanRepo.save(sub);
        return ResponseDto.builder().code(201).message("Member has successfully subscribed to plan").build();
    }

    public SubscriptionResponseDTO getSubscriptionsByMember(UUID memberId) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Member member = memberRepo
                .findByIdAndTenantId(memberId, tenantId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Optional<MemberSubscription> activeSubscriptionOptional = subscriptionPlanRepo.findActiveSubscription(tenantId, memberId);

        List<MemberSubscription> history = subscriptionPlanRepo.findSubscriptionHistory(tenantId,memberId);

        CurrentSubscriptionDto currentSubscriptionDto = null;

        String code = "ACTIVE_SUBSCRIPTION_FOUND";
        String message = "Subscription fetched successfully";

        if (activeSubscriptionOptional.isPresent()) {

            MemberSubscription currentSubscription = activeSubscriptionOptional.get();

            Long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), currentSubscription.getEndDate());

            // Prevent negative values if subscription already expired
            if (remainingDays < 0) {
                remainingDays = 0L;
            }

            currentSubscriptionDto = CurrentSubscriptionDto.builder()
                    .id(currentSubscription.getId().toString())
                    .planName(currentSubscription.getPlan().getName())
                    .status(currentSubscription.getStatus().name())
                    .startDate(currentSubscription.getStartDate().toString())
                    .endDate(currentSubscription.getEndDate().toString())
                    .remainingDays(remainingDays)
                    .duration(currentSubscription.getPlan().getDurationInDays() + " Months")
                    .paymentStatus("Paid")
                    .autoRenew(currentSubscription.getAutoRenew())
                    .build();

        } else {

            code = "NO_ACTIVE_SUBSCRIPTION";
            message = "Member does not have an active subscription";
        }


        return SubscriptionResponseDTO.builder()

                .success(true)
                .code(code)
                .message(message)
                .member(
                        MemberDto.builder()
                                .id(member.getId())
                                .fullName(member.getFullName())
                                .email(member.getEmail())
                                .phoneNumber(member.getPhoneNumber())
                                .profileImageUrl(member.getProfileImageUrl())
                                .build()
                )

                .currentSubscription(currentSubscriptionDto)

                .loyalty(
                        LoyaltyDto.builder()
                                .status("Elite Member")
                                .memberSince(member.getCreatedOn().toLocalDate().toString())
                                .stats("Top active member this month")
                                .progress(70)
                                .nextMilestone("80% Milestone Unlock")
                                .build()
                )

                .paymentMethod(
                        PaymentMethodDto.builder()
                                .type("Visa")
                                .last4("4242")
                                .expiry("12/25")
                                .build()
                )

                .history(
                        history.stream()
                                .map(sub -> SubscriptionHistoryDto.builder()
                                        .id(sub.getId().toString())
                                        .planName(sub.getPlan().getName())
                                        .cycle(
                                                sub.getStartDate() + " - " + sub.getEndDate()
                                        )
                                        .amount(String.valueOf(sub.getPrice()))
                                        .status(sub.getStatus().name())
                                        .build()
                                )
                                .toList()
                )
                .build();
    }

    public ResponseDto renewSubscription(UUID subscriptionId) {

        MemberSubscription sub = getEntity(subscriptionId);

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startDate;

        if (sub.getEndDate().isAfter(today)) {
            startDate = sub.getEndDate();
        } else {
            startDate = today;
        }
        LocalDateTime newEnd = calculateEndDate(startDate, sub.getPlan().getDurationInDays());

        sub.setStartDate(startDate);
        sub.setEndDate(newEnd);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setActive(true);

        subscriptionPlanRepo.save(sub);
        return ResponseDto.builder().code(201).message("Member Subscription Plan has successfully Renewed").build();

    }

    public ResponseDto renewSubscriptionWithPayment(UUID subscriptionId, Payment payment) {

        MemberSubscription sub = getEntity(subscriptionId);

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startDate;

        if (sub.getEndDate().isAfter(today)) {
            startDate = sub.getEndDate();
        } else {
            startDate = today;
        }

        LocalDateTime newEndDate = calculateEndDate(
                startDate,
                sub.getPlan().getDurationInDays()
        );

        sub.setStartDate(startDate);
        sub.setEndDate(newEndDate);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setActive(true);
        sub.setPayment(payment);

        subscriptionPlanRepo.save(sub);
        return ResponseDto.builder().code(201).message("Member Subscription Plan has successfully Renewed").build();
    }

    public ResponseDto cancelSubscription(UUID subscriptionId) {
        MemberSubscription sub = getEntity(subscriptionId);

        sub.setStatus(SubscriptionStatus.CANCELLED);
        sub.setActive(false);

        subscriptionPlanRepo.save(sub);
        return ResponseDto.builder().code(201).message("Member Subscription Plan has Canceled").build();
    }

    private LocalDateTime calculateEndDate(LocalDateTime start, Integer durationDays) {
        return start.plusDays(durationDays);
    }

    private MemberSubscription getEntity(UUID id) {
        return subscriptionPlanRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

    public SubscriptionInsightResponse getInsights() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        List<Object[]> results = subscriptionPlanRepo.getPlanWiseCounts(tenantId);
        long total = subscriptionPlanRepo.getTotalActiveSubscriptions(tenantId);

        List<PlanInsightDTO> plans = results.stream()
                .map(r -> {
                    String name = (String) r[0];
                    long count = (Long) r[1];

                    double percentage = total == 0 ? 0 :
                            (count * 100.0) / total;

                    return PlanInsightDTO.builder()
                            .planName(name)
                            .memberCount(count)
                            .percentage(Math.round(percentage))
                            .build();
                })
                .toList();

        return SubscriptionInsightResponse.builder()
                .plans(plans)
                .totalMembers(total)
                .build();
    }

    public List<MembershipTableDTO> getMemberships() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        List<MemberSubscription> subscriptions =
                subscriptionPlanRepo.findLatestSubscriptions(tenantId);

        return subscriptions.stream().map(sub -> {

            Member member = sub.getMember();
            MembershipPlan plan = sub.getPlan();

            // 🔥 Remaining Days
            int remainingDays = 0;
            if (sub.getEndDate() != null) {
                remainingDays = (int) ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        sub.getEndDate().toLocalDate()
                );
            }

            // 🔥 Status Logic
            String status;
            if (Boolean.TRUE.equals(sub.getActive())) {
                status = "ACTIVE";
            } else if (sub.getStatus() == SubscriptionStatus.PENDING) {
                status = "PENDING";
            } else if (remainingDays <= 0) {
                status = "EXPIRED";
            } else {
                status = "PAUSED";
            }

            // 🔥 Payment Status
            String paymentStatus = sub.getPayment() != null
                    && sub.getPayment().getStatus().name().equals("SUCCESS")
                    ? "PAID"
                    : "PENDING";

            return MembershipTableDTO.builder()
                    .memberId(member.getId())
                    .memberName(member.getFullName())
                    .memberEmail(member.getEmail())
                    .profileImageUrl(member.getProfileImageUrl())

                    .planName(plan != null ? plan.getName() : "-")

                    .status(status)

                    .startDate(sub.getStartDate() != null ? sub.getStartDate().toLocalDate() : null)
                    .endDate(sub.getEndDate() != null ? sub.getEndDate().toLocalDate() : null)

                    .remainingDays(Math.max(remainingDays, 0))

                    .paymentStatus(paymentStatus)

                    .build();

        }).toList();
    }

    public ResponseDto freezeMembership(UUID subscriptionId, FreezeMembershipRequest request) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        MemberSubscription subscription = subscriptionPlanRepo.findByIdAndTenantId(subscriptionId, tenantId)
                        .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new RuntimeException("Subscription already frozen");
        }

        long freezeDays = ChronoUnit.DAYS.between(request.getFreezeStartDate(), request.getFreezeEndDate());

        subscription.setStatus(SubscriptionStatus.FROZEN);

        subscription.setFreezeStartDate(request.getFreezeStartDate().atStartOfDay());

        subscription.setFreezeEndDate(request.getFreezeEndDate().atTime(23, 59));

        subscription.setTotalFreezeDays((int) freezeDays);

        // extend membership end date
        subscription.setEndDate(subscription.getEndDate().plusDays(freezeDays));

        subscriptionPlanRepo.save(subscription);

        return ResponseDto.builder()
                .message("Membership frozen successfully")
                .code(200)
                .build();
    }

    public ResponseDto unfreezeSubscription(UUID subscriptionId, UnfreezeSubscriptionRequest request) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        MemberSubscription subscription = subscriptionPlanRepo.findByIdAndTenantId(subscriptionId, tenantId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (!Boolean.TRUE.equals(subscription.getFrozen())) {
            throw new RuntimeException("Subscription is not frozen");
        }

        subscription.setFrozen(false);

        subscription.setStatus(SubscriptionStatus.ACTIVE);

        subscription.setActualUnfreezeDate(LocalDateTime.now());

        subscription.setFreezeEndDate(LocalDateTime.now());
        subscription.setUnFreezingReason(request.getReason());

        subscriptionPlanRepo.save(subscription);

        return ResponseDto.builder()
                .message("Membership frozen successfully")
                .code(200)
                .build();
    }

    public ResponseDto cancelMembership(UUID subscriptionId, CancelMembershipRequest request) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();


        MemberSubscription subscription = subscriptionPlanRepo.findByIdAndTenantId(subscriptionId, tenantId)
                        .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new RuntimeException("Membership already cancelled");
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setActive(false);
        subscription.setAutoRenew(false);
        subscription.setCancelledOn(LocalDateTime.now());
        subscription.setCancellationReason(request.getReason());

        subscriptionPlanRepo.save(subscription);

        return ResponseDto.builder()
                .message("Membership cancelled successfully")
                .code(200)
                .build();
    }


}
