package com.gym.Elite.Gym.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.integration.entity.Integration;
import com.gym.Elite.Gym.integration.repo.IntegrationRepo;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.tenants.repo.TenantRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IntegrationService {

    private final IntegrationRepo integrationRepo;
    private final TenantRepo tenantRepo;
    private final ValidateService validate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseDto connect(UUID tenantId,
                               String provider,
                               Map<String, Object> config) {

        Tenants tenant = tenantRepo.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        // 🔐 Validate config
        validate.validate(provider, config);

        // 💾 Convert to JSON
        String configJson = toJson(config);

        Integration integration = integrationRepo
                .findByTenantAndProvider(tenant, provider)
                .orElse(new Integration());

        integration.setTenant(tenant);
        integration.setProvider(provider.toUpperCase());
        integration.setConnected(true);
        integration.setStatus("CONNECTED");
        integration.setConfigJson(configJson);
        integration.setCreatedAt(LocalDateTime.now());

        integrationRepo.save(integration);

        return ResponseDto.builder().code(201).message(provider + " Connected Successfully").build();
    }

    private String toJson(Map<String, Object> config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert config");
        }
    }


}
