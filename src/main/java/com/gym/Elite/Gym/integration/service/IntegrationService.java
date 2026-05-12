package com.gym.Elite.Gym.integration.service;

import com.gym.Elite.Gym.integration.client.IntegrationClient;
import com.gym.Elite.Gym.integration.dto.IntegrationCardResponse;
import com.gym.Elite.Gym.integration.dto.IntegrationCatalogResponse;
import com.gym.Elite.Gym.integration.dto.IntegrationRequest;
import com.gym.Elite.Gym.integration.dto.IntegrationResponse;
import com.gym.Elite.Gym.integration.entity.IntegrationRef;
import com.gym.Elite.Gym.integration.entity.IntegrationStatus;
import com.gym.Elite.Gym.integration.entity.IntegrationType;
import com.gym.Elite.Gym.integration.repo.IntegrationRefRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationService {

    private final IntegrationClient integrationClient;
    private final IntegrationRefRepository integrationRefRepository;

    public Map<String, String> getGoogleAuthUrl() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        return integrationClient.getGoogleAuthUrl(tenantId.toString());
    }

    public IntegrationResponse connectIntegration(IntegrationRequest request) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        request.setTenantId(tenantId);

        log.info("Connecting integration: tenantId={}, type={}", tenantId, request.getIntegrationType());

        try {
            IntegrationResponse response = integrationClient.connect( request);
            updateIntegrationRef(tenantId, request.getIntegrationType(), response.getStatus());
            return response;
        } catch (Exception e) {
            log.error("Failed to connect integration: {}", e.getMessage());
            updateIntegrationRef(tenantId, request.getIntegrationType(), IntegrationStatus.FAILED);
            return IntegrationResponse.builder()
                    .integrationType(request.getIntegrationType())
                    .status(IntegrationStatus.FAILED)
                    .message("Connection failed: " + e.getMessage())
                    .build();
        }
    }

    public IntegrationResponse disconnectIntegration(IntegrationType type) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        log.info("Disconnecting integration: tenantId={}, type={}", tenantId, type);

        IntegrationRequest request = IntegrationRequest.builder()
                .tenantId(tenantId)
                .integrationType(type)
                .build();

        try {
            IntegrationResponse response = integrationClient.disconnect( request);
            updateIntegrationRef(tenantId, type, IntegrationStatus.DISCONNECTED);
            return response;
        } catch (Exception e) {
            log.error("Failed to disconnect integration: {}", e.getMessage());
            return IntegrationResponse.builder()
                    .integrationType(type)
                    .status(IntegrationStatus.FAILED)
                    .message("Disconnection failed: " + e.getMessage())
                    .build();
        }
    }

    public List<IntegrationCardResponse> getIntegrationCatalog() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        log.info("Fetching integration catalog for tenant: {}", tenantId);

        return integrationClient.getCatalog();
    }

    private void updateIntegrationRef(UUID tenantId, IntegrationType type, IntegrationStatus status) {
        IntegrationRef ref = integrationRefRepository.findByTenantIdAndIntegrationType(tenantId, type)
                .orElse(IntegrationRef.builder()
                        .tenantId(tenantId)
                        .integrationType(type)
                        .build());
        
        ref.setStatus(status);
        ref.setLastSyncedAt(LocalDateTime.now());
        integrationRefRepository.save(ref);
        
        log.info("Updated IntegrationRef: tenantId={}, type={}, status={}", tenantId, type, status);
    }

    public IntegrationCatalogResponse getIntegrationByService(IntegrationType integrationType) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        log.info("Fetching integration details. tenant={}, service={}", tenantId, integrationType);

        return integrationClient.getDetails(integrationType);
    }
}
