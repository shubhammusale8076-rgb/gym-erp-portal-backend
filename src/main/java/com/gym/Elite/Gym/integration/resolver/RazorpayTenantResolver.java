package com.gym.Elite.Gym.integration.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.tenants.repo.TenantRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RazorpayTenantResolver implements TenantResolver {

    private final TenantRepo tenantRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getProvider() {
        return "razorpay";
    }

    @Override
    public Tenants resolve(String payload) {

        String tenantId = extractTenantIdFromNotes(payload);

        return tenantRepository.findById(UUID.fromString(tenantId))
                .orElseThrow();
    }

    private String extractTenantIdFromNotes(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);

            return node.path("payload")
                    .path("payment")
                    .path("entity")
                    .path("notes")
                    .path("tenantId")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract tenantId from Razorpay payload", e);
        }
    }
}