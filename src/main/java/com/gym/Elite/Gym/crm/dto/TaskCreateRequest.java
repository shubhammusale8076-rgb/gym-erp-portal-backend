package com.gym.Elite.Gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    private LocalDate dueDate;

    private UUID assignedTo;
}
