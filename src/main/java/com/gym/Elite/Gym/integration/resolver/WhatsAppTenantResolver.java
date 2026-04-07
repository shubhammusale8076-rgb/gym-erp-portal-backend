package com.gym.Elite.Gym.integration.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.integration.entity.Integration;
import com.gym.Elite.Gym.integration.repo.IntegrationRepo;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WhatsAppTenantResolver implements TenantResolver {

    private final IntegrationRepo integrationRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getProvider() {
        return "whatsapp";
    }

    @Override
    public Tenants resolve(String payload) {

        try {
            JsonNode node = objectMapper.readTree(payload);

            String phoneNumberId = node.path("entry")
                    .get(0)
                    .path("changes")
                    .get(0)
                    .path("value")
                    .path("metadata")
                    .path("phone_number_id")
                    .asText();

            Integration integration = integrationRepo
                    .findByProviderAndPhoneNumberId("WHATSAPP", phoneNumberId)
                    .orElseThrow(() -> new RuntimeException("Integration not found"));

            return integration.getTenant();

        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve tenant", e);
        }
    }
}
