package com.gym.Elite.Gym.crm.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppRequest {
    private java.util.UUID tenantId;
    private String correlationId;
    private java.util.UUID leadId;
    private String phone;
    private String template;
    private Map<String, String> variables;
}
