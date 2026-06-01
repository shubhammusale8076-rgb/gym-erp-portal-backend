package com.gym.Elite.Gym.auth.dto.memberDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDTO {

    private UUID id;
    private String name;        // fullName → name
    private String email;
    private String profileImg;
    private String phoneName;
    private String plan;        // NEW (can be default or from membership)
    private String accountStatus;
    private String membershipStatus;
    private String paymentStatus;
    private LocalDateTime joinDate;    // formatted date (UI-ready)
}

