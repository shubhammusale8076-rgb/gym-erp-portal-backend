package com.gym.Elite.Gym.integration.dto.google;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GooglePasswordResetRequestDto {

    private UUID tenantId;

    private String email;

    private String memberName;

    private String temporaryPassword;
}