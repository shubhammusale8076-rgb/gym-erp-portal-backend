package com.gym.Elite.Gym.integration.dto.whatsapp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WelcomeMessage {

    private UUID tenantId;
    private String correlationId;
    private String memberName;
    private String planName;
    private String phoneNumber;
    private String  planStartDate;
    private String trainerName;

}
