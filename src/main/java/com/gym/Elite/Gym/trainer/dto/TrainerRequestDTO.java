package com.gym.Elite.Gym.trainer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerRequestDTO {

    private String fullName;
    private String email;
    private String phoneNumber;

    private Integer experienceInYears;

    // Website profile
    private String bio;
    private String profileImageUrl;

    private List<String> skills; // Yoga, HIIT
    private String certifications;

    // Social links
    private String instagramHandle;
    private String linkedinUrl;

    // Availability
    private List<String> availableDays; // MON, TUE
    private String morningShiftStart;
    private String morningShiftEnd;
    private String eveningShiftStart;
    private String eveningShiftEnd;

    // Status
    private Boolean available;
    private Boolean visibleOnWebsite;
    private Boolean featured;
}
