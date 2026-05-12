package com.gym.Elite.Gym.crm.dto;

import com.gym.Elite.Gym.crm.enums.LeadPriority;
import com.gym.Elite.Gym.crm.enums.LeadSource;
import com.gym.Elite.Gym.crm.enums.LeadStage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponseDto {

    private UUID id;
    private String fullName;
    private String phone;
    private String email;
    private LeadSource source;
    private LeadStage stage;
    private LeadPriority priority;
    private Integer leadScore;
    private Double expectedRevenue;
    private String fitnessGoal;
    private String nextAction;
    private LocalDateTime nextFollowUpAt;
    private Boolean followUpOverdue;
    private Boolean converted;
    private UUID assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
