package com.gym.Elite.Gym.integration.parser.razorpay;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.integration.parser.WebhookParser;
import org.springframework.stereotype.Component;

@Component
public class RazorpayWebhookParser implements WebhookParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getProvider() {
        return "razorpay";
    }

    @Override
    public String extractEventId(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            return node.get("payload")
                    .get("payment")
                    .get("entity")
                    .get("id")
                    .asText(); // payment_id
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract eventId");
        }
    }

    @Override
    public String extractTenantId(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);

            return node.get("payload")
                    .get("payment")
                    .get("entity")
                    .get("notes")
                    .get("tenantId")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract tenantId");
        }
    }

    @Override
    public String extractEventType(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            return node.get("event").asText(); // payment.captured
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract eventType");
        }
    }
}
