package com.gym.Elite.Gym.auth.dto.memberDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRequestDTO {

    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;
    private String address;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String aadhaarNumber; // 🔒 sensitive
    private String profileImageUrl;
    private String profileImagePublicId;
    private Integer durationInDays;
    private UUID planId;
}
