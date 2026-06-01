package com.gym.Elite.Gym.crm.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    private UUID tenantId;
    private String correlationId;
    private String to;
    private String subject;
    private String template;
    private Map<String, String> variables;
}
