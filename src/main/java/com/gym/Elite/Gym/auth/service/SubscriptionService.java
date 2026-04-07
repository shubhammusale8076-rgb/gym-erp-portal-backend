package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.subscriptionDto.SubscriptionRequestDTO;
import com.gym.Elite.Gym.auth.dto.subscriptionDto.SubscriptionResponseDTO;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.entity.MemberSubscription;
import com.gym.Elite.Gym.auth.entity.MembershipPlan;
import com.gym.Elite.Gym.auth.mapper.SubscriptionPlanMapper;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.auth.repo.MembershipPlanRepo;
import com.gym.Elite.Gym.auth.repo.SubscriptionPlanRepo;
import com.gym.Elite.Gym.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final MemberRepo memberRepo;
    private final MembershipPlanRepo membershipPlanRepo;
    private final SubscriptionPlanRepo subscriptionPlanRepo;
    private final SubscriptionPlanMapper subscriptionPlanMapper;

    public ResponseDto createSubscription(SubscriptionRequestDTO request) {

        Member member = memberRepo.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        MembershipPlan plan = membershipPlanRepo.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        Date startDate = new Date();
        Date endDate = calculateEndDate(startDate, plan.getDurationInDays());

        MemberSubscription subscription = MemberSubscription.builder()
                .member(member)
                .plan(plan)
                .startDate(startDate)
                .endDate(endDate)
                .active(true)
                .autoRenew(request.getAutoRenew())
                .remainingSessions(plan.getSessionLimit())
                .status("ACTIVE")
                .createdOn(new Date())
                .build();

        subscriptionPlanRepo.save(subscription);
        return ResponseDto.builder().code(201).message("Member has successfully subscribed to plan").build();
    }

    public ResponseDto createSubscriptionFromPayment(UUID memberId, UUID planId, Payment payment) {

        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        MembershipPlan plan = membershipPlanRepo.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        Date startDate = new Date();
        Date endDate = calculateEndDate(startDate, plan.getDurationInDays());

        MemberSubscription sub = MemberSubscription.builder()
                .member(member)
                .plan(plan)
                .startDate(startDate)
                .endDate(endDate)
                .status("ACTIVE")
                .active(true)
                .autoRenew(false)
                .remainingSessions(plan.getSessionLimit())
                .payment(payment)
                .createdOn(new Date())
                .build();

        subscriptionPlanRepo.save(sub);
        return ResponseDto.builder().code(201).message("Member has successfully subscribed to plan").build();
    }

    public List<SubscriptionResponseDTO> getSubscriptionsByMember(UUID memberId) {
        return subscriptionPlanRepo.findByMemberId(memberId)
                .stream()
                .map(subscriptionPlanMapper::mapToSubscriptionDTO)
                .toList();
    }

    public ResponseDto renewSubscription(UUID subscriptionId) {

        MemberSubscription sub = getEntity(subscriptionId);

        Date today = new Date();
        Date startDate;

        // 🔥 KEY LOGIC
        if (sub.getEndDate().after(today)) {
            // active → extend
            startDate = sub.getEndDate();
        } else {
            // expired → restart
            startDate = today;
        }
        Date newEnd = calculateEndDate(startDate, sub.getPlan().getDurationInDays());

        sub.setStartDate(startDate);
        sub.setEndDate(newEnd);
        sub.setStatus("ACTIVE");
        sub.setActive(true);
        sub.setUpdatedOn(new Date());

        subscriptionPlanRepo.save(sub);
        return ResponseDto.builder().code(201).message("Member Subscription Plan has successfully Renewed").build();

    }

    public ResponseDto renewSubscriptionWithPayment(UUID subscriptionId, Payment payment) {

        MemberSubscription sub = getEntity(subscriptionId);

        Date today = new Date();

        Date startDate;

        // 🔥 KEY LOGIC
        if (sub.getEndDate().after(today)) {
            // active → extend
            startDate = sub.getEndDate();
        } else {
            // expired → restart
            startDate = today;
        }

        Date newEndDate = calculateEndDate(
                startDate,
                sub.getPlan().getDurationInDays()
        );

        sub.setStartDate(startDate);
        sub.setEndDate(newEndDate);
        sub.setStatus("ACTIVE");
        sub.setActive(true);
        sub.setPayment(payment);
        sub.setUpdatedOn(new Date());

        subscriptionPlanRepo.save(sub);
        return ResponseDto.builder().code(201).message("Member Subscription Plan has successfully Renewed").build();
    }

    public ResponseDto cancelSubscription(UUID subscriptionId) {
        MemberSubscription sub = getEntity(subscriptionId);

        sub.setStatus("CANCELLED");
        sub.setActive(false);
        sub.setUpdatedOn(new Date());

        subscriptionPlanRepo.save(sub);
        return ResponseDto.builder().code(201).message("Member Subscription Plan has Canceled").build();
    }

    private Date calculateEndDate(Date start, Integer durationDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.DAY_OF_MONTH, durationDays);
        return cal.getTime();
    }

    private MemberSubscription getEntity(UUID id) {
        return subscriptionPlanRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

}
