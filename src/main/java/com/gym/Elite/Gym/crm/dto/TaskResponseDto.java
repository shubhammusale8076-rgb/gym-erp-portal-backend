package com.gym.Elite.Gym.crm.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {

    private UUID id;
    private UUID leadId;
    private String title;
    private Boolean completed;
    private LocalDate dueDate;
    private UUID assignedTo;
    private LocalDateTime createdAt;
}
