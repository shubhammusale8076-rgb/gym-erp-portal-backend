package com.gym.Elite.Gym.integration.processor.whatsapp;

import com.gym.Elite.Gym.integration.entity.WebhookEvent;
import com.gym.Elite.Gym.integration.processor.EventProcessor;
import org.springframework.stereotype.Component;

@Component
public class WhatsAppEventProcessor implements EventProcessor {

    @Override
    public String getProvider() {
        return "WHATSAPP";
    }

    @Override
    public void process(WebhookEvent event) {

        // Example:
        // user sends "Hi"

        // future:
        // - auto reply
        // - lead capture
        // - chatbot
    }
}
