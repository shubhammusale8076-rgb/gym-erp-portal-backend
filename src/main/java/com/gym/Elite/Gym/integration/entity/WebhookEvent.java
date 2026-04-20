package com.gym.Elite.Gym.integration.entity;

import com.gym.Elite.Gym.tenants.entity.Tenants;
import lombok.Data;

@Data
public class WebhookEvent {
    private String eventType;
    private Tenants tenant;
    private Object payload;
    private String signature;
    private String providerEventId;
    private String rawBody; // Added for signature verification
}
