package com.gym.Elite.Gym.trainer.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TrainerListDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String profileImageUrl;
    private List<String> skills;
    private Integer experienceInYears;
    private Long assignedMembersCount;
    private Boolean available;

}
