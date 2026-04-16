package com.gym.Elite.Gym.integration.handler;

import com.gym.Elite.Gym.integration.dto.EventRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentInitiatedHandler implements EventHandler {
    @Override
    public void handle(EventRequest event) {
        log.info("Handling PAYMENT_INITIATED event: {}", event);
        // Add business logic for payment initiation if needed
    }

    @Override
    public String getEventType() {
        return "PAYMENT_INITIATED";
    }
}
