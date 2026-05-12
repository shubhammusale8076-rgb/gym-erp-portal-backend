package com.gym.Elite.Gym.crm.dto;

import com.gym.Elite.Gym.crm.enums.FollowUpStatus;
import com.gym.Elite.Gym.crm.enums.FollowUpType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpResponseDto {

    private UUID id;
    private UUID leadId;
    private String leadName;
    private LocalDateTime followUpAt;
    private FollowUpType type;
    private FollowUpStatus status;
    private String remarks;
    private Boolean completed;
    private Boolean overdue;
    private UUID assignedTo;
    private LocalDateTime createdAt;
}
