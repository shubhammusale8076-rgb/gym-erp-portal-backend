package com.gym.Elite.Gym.auth.mapper;

import com.gym.Elite.Gym.auth.dto.memberDto.MemberResponseDTO;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.entity.MemberSubscription;
import com.gym.Elite.Gym.auth.entity.SubscriptionStatus;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class MemberMapper {

    public MemberResponseDTO mapToDTO(Member member) {

        MemberSubscription sub = member.getSubscriptions()
                .stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .findFirst()
                .orElseGet(() ->
                        member.getSubscriptions()
                                .stream()
                                .max(Comparator.comparing(MemberSubscription::getCreatedOn))
                                .orElse(null)
                );

        String plan = null;
        String status = null;
        String paymentStatus = null;

        if (sub != null) {
            plan = sub.getPlan() != null ? sub.getPlan().getName() : null;
            status = sub.getStatus() != null ? sub.getStatus().name() : null;
            paymentStatus = sub.getPayment() != null ?sub.getPayment().getStatus().name() : null;
        }

        return MemberResponseDTO.builder()
                .id(member.getId())
                .name(member.getFullName())
                .email(member.getEmail())
                .profileImg(member.getProfileImageUrl())
                .phoneName(member.getPhoneNumber())
                .plan(plan)
                .accountStatus(String.valueOf(member.getActive()))
                .membershipStatus(status)
                .paymentStatus(paymentStatus)
                .joinDate(member.getCreatedOn())
                .build();
    }
}
