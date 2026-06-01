package com.gym.Elite.Gym.auth.dto.memberDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResponseDto {

    private UUID id;
    private String fullName;
    private String email;
    private String profileImage;
}
