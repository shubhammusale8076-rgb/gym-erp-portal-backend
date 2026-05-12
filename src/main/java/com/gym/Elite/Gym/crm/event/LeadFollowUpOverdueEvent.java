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
@NoArgsConstructor
@AllArgsConstructor
public class LeadFollowUpOverdueEvent extends BaseCrmEvent {
    private Long followUpId;
    private LocalDateTime scheduledAt;
}
