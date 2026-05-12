package com.gym.Elite.Gym.integration.dto;

import com.gym.Elite.Gym.integration.entity.IntegrationStatus;
import com.gym.Elite.Gym.integration.entity.IntegrationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationResponse {
    private IntegrationType integrationType;
    private IntegrationStatus status; // CONNECTED, FAILED, DISCONNECTED
    private String message;
}
