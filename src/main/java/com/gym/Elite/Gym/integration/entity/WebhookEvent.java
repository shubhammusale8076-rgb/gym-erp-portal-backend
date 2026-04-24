package com.gym.Elite.Gym.integration.entity;

import lombok.Data;
import java.util.UUID;

/**
 * Non-JPA DTO for inbound webhook events.
 * Uses tenantId UUID instead of the Tenants entity to remain
 * decoupled from the JPA layer.
 */
@Data
public class WebhookEvent {
    private String eventType;
    private UUID tenantId;
    private Object payload;
    private String signature;
    private String providerEventId;
    private String rawBody;
}
