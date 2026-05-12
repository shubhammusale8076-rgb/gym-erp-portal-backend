package com.gym.Elite.Gym.crm.dto;

import com.gym.Elite.Gym.crm.enums.LeadPriority;
import com.gym.Elite.Gym.crm.enums.LeadSource;
import com.gym.Elite.Gym.crm.enums.LeadStage;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadCreateRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Invalid phone number format")
    private String phone;

    @Email(message = "Invalid email address")
    private String email;

    @NotNull(message = "Lead source is required")
    private LeadSource source;

    private LeadStage stage;

    private Double expectedRevenue;

    private String fitnessGoal;

    private String nextAction;

    private LocalDateTime nextFollowUpAt;

    private UUID assignedTo;
}
