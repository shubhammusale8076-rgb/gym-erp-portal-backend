package com.gym.Elite.Gym.integration.dto;

import com.gym.Elite.Gym.integration.entity.IntegrationStatus;
import com.gym.Elite.Gym.integration.entity.IntegrationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationCatalogResponse {

    private UUID id;
    private IntegrationType service;
    private String displayName;
    private String description;
    private String icon;
    private String iconColor;
    private String iconBg;
    private IntegrationAuthType authType;
    private boolean active;
    private String configSchema;
    private boolean connected;
    private boolean enabled;
    private String mode;
    private IntegrationStatus status;
    private String email;
    private LocalDateTime connectedAt;
    private LocalDateTime updatedAt;
    // =====================================
    // WEBHOOKS
    // =====================================
    private boolean supportsWebhooks;

    private String webhookUrl;

    // =====================================
    // EVENTS
    // =====================================
    private List<String> supportedEvents;

    // =====================================
    // PROVIDER DATA
    // =====================================
    private Map<String, Object> metadata;
}
