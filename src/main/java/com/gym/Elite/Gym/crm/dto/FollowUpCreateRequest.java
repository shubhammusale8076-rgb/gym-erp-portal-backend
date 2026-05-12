package com.gym.Elite.Gym.crm.dto;

import com.gym.Elite.Gym.crm.enums.FollowUpType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpCreateRequest {

    @NotNull(message = "Lead ID is required")
    private UUID leadId;

    @NotNull(message = "Follow-up date/time is required")
    private LocalDateTime followUpAt;

    @NotNull(message = "Follow-up type is required")
    private FollowUpType type;

    private String remarks;

    private UUID assignedTo;
}
