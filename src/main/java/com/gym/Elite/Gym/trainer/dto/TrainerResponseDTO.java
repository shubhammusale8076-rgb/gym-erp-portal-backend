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
public class TrainerResponseDTO {

    private UUID id;

    private String fullName;
    private String email;
    private String phoneNumber;

    private Integer experienceInYears;

    private String bio;
    private String profileImageUrl;

    private List<String> skills;
    private String certifications;

    private String instagramHandle;
    private String linkedinUrl;

    private List<String> availableDays;
    private String morningShiftStart;
    private String morningShiftEnd;
    private String eveningShiftStart;
    private String eveningShiftEnd;

    private Boolean available;
    private Boolean active;
    private Boolean visibleOnWebsite;
    private Boolean featured;
}
