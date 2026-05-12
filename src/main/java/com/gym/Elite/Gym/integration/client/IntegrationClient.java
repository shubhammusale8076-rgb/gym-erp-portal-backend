package com.gym.Elite.Gym.integration.client;

import com.gym.Elite.Gym.common.config.FeignClientConfig;
import com.gym.Elite.Gym.integration.dto.IntegrationCardResponse;
import com.gym.Elite.Gym.integration.dto.IntegrationCatalogResponse;
import com.gym.Elite.Gym.integration.dto.IntegrationRequest;
import com.gym.Elite.Gym.integration.dto.IntegrationResponse;
import com.gym.Elite.Gym.integration.dto.google.GoogleSheetExportRequest;
import com.gym.Elite.Gym.integration.dto.google.GoogleSheetExportResponse;
import com.gym.Elite.Gym.integration.entity.IntegrationType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(
        name = "integration-client",
        url = "${integration.service.url}",
        configuration = FeignClientConfig.class
)
public interface IntegrationClient {

    @PostMapping("/internal/integrations/connect")
    IntegrationResponse connect(
            @RequestBody IntegrationRequest request
    );

    @PostMapping("/internal/integrations/disconnect")
    IntegrationResponse disconnect(
            @RequestBody IntegrationRequest request
    );

    @GetMapping("/api/google/connect")
    Map<String, String> getGoogleAuthUrl(
            @RequestParam String tenantId
    );

    @GetMapping("/api/integrations/catalog")
    List<IntegrationCardResponse> getCatalog();

    @GetMapping("/api/integrations/service/{integrationType}")
    IntegrationCatalogResponse getDetails(@PathVariable("integrationType") IntegrationType integrationType);

    @PostMapping("/internal/google/export-members")
    GoogleSheetExportResponse exportMembersToGoogleSheets(@RequestBody GoogleSheetExportRequest request);
}
