package com.gym.Elite.Gym.integration.dto;

import com.gym.Elite.Gym.integration.entity.IntegrationStatus;
import com.gym.Elite.Gym.integration.entity.IntegrationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationCardResponse {

    private UUID id;
    private IntegrationType service;
    private String displayName;
    private String description;
    private String icon;
    private String iconColor;
    private String iconBg;
    private IntegrationAuthType authType;
    private boolean connected;
    private boolean enabled;
    private IntegrationStatus status;
}