package com.gym.Elite.Gym.auth.dto.authDtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityResponseDto {

    private UUID id;
    private String roleCode;
    private String roleDescription;
    private Long userCount; // 👈 NEW FIELD
}

