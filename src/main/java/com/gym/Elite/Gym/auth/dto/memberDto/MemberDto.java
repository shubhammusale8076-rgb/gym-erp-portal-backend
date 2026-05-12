package com.gym.Elite.Gym.auth.dto.memberDto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
}
