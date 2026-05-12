package com.gym.Elite.Gym.crm.dto;

import com.gym.Elite.Gym.crm.enums.LeadPriority;
import com.gym.Elite.Gym.crm.enums.LeadSource;
import com.gym.Elite.Gym.crm.enums.LeadStage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadDetailsDto {

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

    // ─── Nested collections ──────────────────────────────────────────────────

    private List<FollowUpResponseDto> followUps;
    private List<ActivityResponseDto> activities;
    private List<NoteResponseDto> notes;
    private List<TaskResponseDto> tasks;

    // ─── Computed stats ──────────────────────────────────────────────────────

    private Integer totalFollowUps;
    private Integer completedFollowUps;
    private Integer pendingTasks;
}
