package com.gym.Elite.Gym.trainer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerAssignmentDTO {

    private UUID id;
    private String fullName;

    private Integer capacity;         // e.g. 15
    private Long assignedCount;       // e.g. 12

    private List<String> skills;

    private Boolean available;
}
