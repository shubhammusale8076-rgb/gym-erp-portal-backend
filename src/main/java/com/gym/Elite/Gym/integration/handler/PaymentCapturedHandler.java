package com.gym.Elite.Gym.integration.handler;

import com.gym.Elite.Gym.integration.dto.EventRequest;
import com.gym.Elite.Gym.integration.entity.WebhookEvent;
import com.gym.Elite.Gym.integration.service.whatsapp.WhatsAppService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentCapturedHandler implements EventHandler {

    private final WhatsAppService whatsAppService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(EventRequest event) {
        log.info("Handling PAYMENT_CAPTURED event: {}", event);

        try {
            // Re-map payload back to WebhookEvent or appropriate object if needed
            // For now, restoring the logic decoupled from RazorpayEventProcessor
            // event.getPayload() contains the WebhookEvent data

            String payloadJson = objectMapper.writeValueAsString(event.getPayload());
            WebhookEvent webhookEvent = objectMapper.readValue(payloadJson, WebhookEvent.class);

            String phone = "+919876543210"; // This was hardcoded in RazorpayEventProcessor

            whatsAppService.sendMessage(
                    webhookEvent.getTenant(),
                    phone,
                    "✅ Payment received! Welcome to the gym 💪"
            );

            log.info("WhatsApp message sent successfully for tenant: {}", event.getTenantId());

        } catch (Exception e) {
            log.error("Failed to handle PAYMENT_CAPTURED event: {}", e.getMessage());
        }
    }

    @Override
    public String getEventType() {
        return "PAYMENT_CAPTURED";
    }
}
