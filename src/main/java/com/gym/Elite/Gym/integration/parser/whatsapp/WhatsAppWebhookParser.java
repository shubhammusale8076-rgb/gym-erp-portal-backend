package com.gym.Elite.Gym.integration.parser.whatsapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.integration.parser.WebhookParser;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WhatsAppWebhookParser implements WebhookParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getProvider() {
        return "whatsapp";
    }

    @Override
    public String extractEventId(String payload) {

        return UUID.randomUUID().toString(); // no strict ID from WA
    }

    @Override
    public String extractTenantId(String payload) {
        return null;
    }

    @Override
    public String extractEventType(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);

            JsonNode messagesNode = node.path("entry")
                    .path(0)
                    .path("changes")
                    .path(0)
                    .path("value")
                    .path("messages");

            if (!messagesNode.isMissingNode() && messagesNode.size() > 0) {
                return "message.received";
            }

            return "unknown";

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract event type", e);
        }
    }


}
