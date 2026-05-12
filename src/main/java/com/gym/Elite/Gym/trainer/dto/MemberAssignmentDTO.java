package com.gym.Elite.Gym.trainer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAssignmentDTO {

    private UUID id;
    private String fullName;
    private String email;

    private String plan; // PLATINUM / ELITE / STANDARD

    private UUID trainerId;
    private String trainerName; // currentTrainer

    private String status; // ASSIGNED / UNASSIGNED / PENDING
}
