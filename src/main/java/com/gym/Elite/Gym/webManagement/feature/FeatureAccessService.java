package com.gym.Elite.Gym.webManagement.feature;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeatureAccessService {

    private final TenantFeatureRepository tenantFeatureRepository;

    public boolean isFeatureEnabled(UUID tenantId, FeatureModule module) {
        return tenantFeatureRepository.findByTenantIdAndFeatureModule(tenantId, module)
                .map(feature -> feature.getEnabled() && 
                        (feature.getExpiresAt() == null || feature.getExpiresAt().isAfter(LocalDateTime.now())))
                .orElse(false);
    }

    public void validateAccess(UUID tenantId, FeatureModule module) {
        if (!isFeatureEnabled(tenantId, module)) {
            throw new RuntimeException("Access Denied: Feature " + module + " is not enabled for this tenant.");
        }
    }
}
