package com.gym.Elite.Gym.crm.dto;

import com.gym.Elite.Gym.crm.enums.ActivityType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponseDto {

    private UUID id;
    private UUID leadId;
    private ActivityType type;
    private String title;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
}
