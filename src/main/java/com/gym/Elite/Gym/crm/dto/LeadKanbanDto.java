package com.gym.Elite.Gym.crm.dto;

import com.gym.Elite.Gym.crm.enums.LeadPriority;
import com.gym.Elite.Gym.crm.enums.LeadSource;
import com.gym.Elite.Gym.crm.enums.LeadStage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Minimal DTO for Kanban board drag-and-drop views.
 * Only contains fields needed for card rendering.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadKanbanDto {

    private UUID id;
    private String fullName;
    private String phone;
    private LeadStage stage;
    private LeadPriority priority;
    private Integer leadScore;
    private LeadSource source;
    private UUID assignedTo;
    private Boolean followUpOverdue;
    private LocalDateTime nextFollowUpAt;
    private Double expectedRevenue;
}
