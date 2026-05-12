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

    // 🔹 BASIC
    private String fullName;
    private String email;
    private String phoneNumber;
    private Integer experienceInYears;

    // 🔹 PROFILE
    private String bio;
    private String profileImageUrl;
    private List<String> skills;
    private String certifications;

    // 🔹 SOCIAL
    private String instagramHandle;
    private String linkedinUrl;

    // 🔹 AVAILABILITY
    private List<TrainerAvailabilityDTO> availability;

    // 🔹 STATUS
    private Boolean available;
    private Boolean active;
    private Boolean visibleOnWebsite;
    private Boolean featured;

    // 🔥 KPI (computed)
    private Double memberSatisfaction;     // e.g. 4.9
    private Integer sessionsCompleted;     // monthly or total
    private Double retentionRate;          // %
    private Integer currentRosterCount;

    // 🔥 ROSTER (IMPORTANT)
    private List<TrainerMemberDTO> assignedMembers;
}
