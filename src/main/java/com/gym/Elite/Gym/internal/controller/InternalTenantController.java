package com.gym.Elite.Gym.internal.controller;

import com.gym.Elite.Gym.internal.dto.TenantRefRequest;
import com.gym.Elite.Gym.tenants.entity.TenantRef;
import com.gym.Elite.Gym.tenants.service.TenantRefService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Internal-only API for syncing tenant data from the Admin Panel.
 * This endpoint is NOT exposed to end users or tenant clients.
 * Protected by a shared X-Internal-Secret header.
 *
 * Flow:
 *   Admin Panel creates tenant → calls POST /internal/tenants
 *   Core App stores TenantRef (lightweight reference)
 *   Core App can now validate tenant before creating owner or other domain objects
 */
@RestController
@RequestMapping("/internal/tenants")
@RequiredArgsConstructor
public class InternalTenantController {

    private final TenantRefService tenantRefService;

    @Value("${internal.api.secret}")
    private String internalSecret;

    @PostMapping
    public ResponseEntity<?> upsertTenant(
            @RequestHeader(value = "X-Internal-Secret", required = false) String secret,
            @RequestBody TenantRefRequest request) {

        if (secret == null || !secret.equals(internalSecret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid or missing internal secret"));
        }

        if (request.getTenantId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "tenantId is required"));
        }

        if (request.getPlanCode() == null || request.getPlanCode().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "planCode is required"));
        }

        try {
            TenantRef saved = tenantRefService.upsertTenant(request);
            return ResponseEntity.ok(Map.of(
                    "tenantId", saved.getTenantId(),
                    "name",     saved.getName() != null ? saved.getName() : "",
                    "status",   saved.getStatus().name(),
                    "planCode", saved.getPlanCode(),
                    "message",  "TenantRef synced successfully"
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
