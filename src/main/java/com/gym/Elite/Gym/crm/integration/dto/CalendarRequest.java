package com.gym.Elite.Gym.crm.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarRequest {
    private java.util.UUID tenantId;
    private String correlationId;
    private java.util.UUID leadId;
    private String title;
    private String description;
    private java.time.LocalDateTime startTime;
    private java.time.LocalDateTime endTime;
    private java.util.List<String> attendees;
}
