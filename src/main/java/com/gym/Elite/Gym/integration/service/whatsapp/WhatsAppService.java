package com.gym.Elite.Gym.integration.service.whatsapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.integration.entity.Integration;
import com.gym.Elite.Gym.integration.entity.WhatsAppConfig;
import com.gym.Elite.Gym.integration.repo.IntegrationRepo;
import com.gym.Elite.Gym.integration.utill.WhatsAppApiClient;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WhatsAppService {

    private final IntegrationRepo integrationRepo;
    private final WhatsAppApiClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public WhatsAppService(IntegrationRepo integrationRepo,
                           WhatsAppApiClient client) {
        this.integrationRepo = integrationRepo;
        this.client = client;
    }

    public void sendMessage(Tenants tenant, String to, String message) {

        Integration integration = integrationRepo
                .findByTenantAndProvider(tenant, "WHATSAPP")
                .orElseThrow(() -> new RuntimeException("WhatsApp not connected"));

        WhatsAppConfig config = parseConfig(integration.getConfigJson());

        client.sendMessage(
                config.getPhoneNumberId(),
                config.getAccessToken(),
                to,
                message
        );
    }

    private WhatsAppConfig parseConfig(String json) {
        try {
            return objectMapper.readValue(json, WhatsAppConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse WhatsApp config", e);
        }
    }
}
