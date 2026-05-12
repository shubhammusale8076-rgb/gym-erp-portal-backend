package com.gym.Elite.Gym.auth.dto.subscriptionDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembershipTableDTO {

    private UUID memberId;
    private String memberName;
    private String memberEmail;
    private String profileImageUrl;
    private String planName;
    private String status; // ACTIVE / PENDING / EXPIRED / PAUSED
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer remainingDays;
    private String paymentStatus; // PAID / PENDING
}
