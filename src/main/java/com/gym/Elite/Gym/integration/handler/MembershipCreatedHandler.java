package com.gym.Elite.Gym.integration.handler;

import com.gym.Elite.Gym.integration.dto.EventRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MembershipCreatedHandler implements EventHandler {
    @Override
    public void handle(EventRequest event) {
        log.info("Handling MEMBERSHIP_CREATED event: {}", event);
        // Add business logic for membership creation if needed
    }

    @Override
    public String getEventType() {
        return "MEMBERSHIP_CREATED";
    }
}
