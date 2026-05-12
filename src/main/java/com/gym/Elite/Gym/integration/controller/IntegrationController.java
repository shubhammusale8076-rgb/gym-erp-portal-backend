package com.gym.Elite.Gym.integration.controller;

import com.gym.Elite.Gym.integration.dto.IntegrationCardResponse;
import com.gym.Elite.Gym.integration.dto.IntegrationCatalogResponse;
import com.gym.Elite.Gym.integration.dto.IntegrationRequest;
import com.gym.Elite.Gym.integration.dto.IntegrationResponse;
import com.gym.Elite.Gym.integration.entity.IntegrationRef;
import com.gym.Elite.Gym.integration.entity.IntegrationType;
import com.gym.Elite.Gym.integration.service.IntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integrations")
@RequiredArgsConstructor
public class IntegrationController {

    private final IntegrationService integrationService;


    @GetMapping("/google/connect")
    public ResponseEntity<?> connectGoogle() {
        return ResponseEntity.ok(integrationService.getGoogleAuthUrl());
    }

    @PostMapping("/connect")
    public ResponseEntity<IntegrationResponse> connect(@RequestBody IntegrationRequest request) {
        return ResponseEntity.ok(integrationService.connectIntegration(request));
    }

    @PostMapping("/disconnect")
    public ResponseEntity<IntegrationResponse> disconnect(@RequestParam IntegrationType type) {
        return ResponseEntity.ok(integrationService.disconnectIntegration(type));
    }

    @GetMapping
    public ResponseEntity<List<IntegrationCardResponse>> getIntegrations() {
        return ResponseEntity.ok(integrationService.getIntegrationCatalog());
    }

    @GetMapping("/service/{integrationType}")
    public ResponseEntity<IntegrationCatalogResponse> getIntegrationByService(@PathVariable IntegrationType integrationType) {
        return ResponseEntity.ok(integrationService.getIntegrationByService(integrationType));
    }
}
