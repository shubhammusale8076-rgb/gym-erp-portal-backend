package com.gym.Elite.Gym.tenants.service;

import com.gym.Elite.Gym.internal.dto.TenantRefRequest;
import com.gym.Elite.Gym.tenants.entity.TenantRef;
import com.gym.Elite.Gym.tenants.entity.TenantRef.TenantStatus;
import com.gym.Elite.Gym.tenants.repo.TenantRefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TenantRefService {

    private final TenantRefRepository tenantRefRepository;

    /**
     * Creates or updates a TenantRef from data synced by the Admin Panel.
     * If the tenant already exists, all mutable fields are updated.
     * If not, a new TenantRef record is inserted.
     */
    public TenantRef upsertTenant(TenantRefRequest request) {
        TenantStatus status = parseStatus(request.getStatus());

        Optional<TenantRef> existing = tenantRefRepository.findById(request.getTenantId());

        if (existing.isPresent()) {
            TenantRef ref = existing.get();
            ref.setName(request.getName());
            ref.setStatus(status);
            ref.setPlanCode(request.getPlanCode());
            ref.setUpdatedAt(LocalDateTime.now());
            return tenantRefRepository.save(ref);
        }

        TenantRef newRef = TenantRef.builder()
                .tenantId(request.getTenantId())
                .name(request.getName())
                .status(status)
                .planCode(request.getPlanCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return tenantRefRepository.save(newRef);
    }

    /**
     * Validates that a tenant exists and is ACTIVE.
     * Used as a guard in domain operations (e.g., createOwner, createMember).
     *
     * @param tenantId the tenant UUID to validate
     * @throws RuntimeException if tenant is not found or not ACTIVE
     */
    public TenantRef validateActiveTenant(UUID tenantId) {
        TenantRef ref = tenantRefRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException(
                        "Tenant not found in Core App registry. Please sync tenant first via /internal/tenants"));

        if (ref.getStatus() != TenantStatus.ACTIVE) {
            throw new RuntimeException(
                    "Tenant is not ACTIVE. Current status: " + ref.getStatus());
        }

        return ref;
    }

    private TenantStatus parseStatus(String status) {
        try {
            return TenantStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new RuntimeException("Invalid tenant status: '" + status + "'. Must be ACTIVE or SUSPENDED.");
        }
    }
}
