package com.gym.Elite.Gym.integration.handler;

import com.gym.Elite.Gym.integration.dto.EventRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookingCreatedHandler implements EventHandler {
    @Override
    public void handle(EventRequest event) {
        log.info("Handling BOOKING_CREATED event: {}", event);
        // Add business logic for booking creation if needed
    }

    @Override
    public String getEventType() {
        return "BOOKING_CREATED";
    }
}
