package com.gym.Elite.Gym.crm.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
public class WhatsAppMessageRequestedEvent extends BaseCrmEvent {
    private String phone;
    private String template;
    private Map<String, String> variables;
}
