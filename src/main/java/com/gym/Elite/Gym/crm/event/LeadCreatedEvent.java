package com.gym.Elite.Gym.crm.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LeadCreatedEvent extends BaseCrmEvent {
    private String source;
    private String name;
    private String phone;
}
