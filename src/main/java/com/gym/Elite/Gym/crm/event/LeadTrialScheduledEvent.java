package com.gym.Elite.Gym.crm.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
public class LeadTrialScheduledEvent extends BaseCrmEvent {
    private LocalDateTime trialDateTime;
    private String trialType;
    private String notes;
}
