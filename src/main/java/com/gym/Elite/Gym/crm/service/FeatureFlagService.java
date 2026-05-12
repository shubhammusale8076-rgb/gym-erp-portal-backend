package com.gym.Elite.Gym.crm.service;

import com.gym.Elite.Gym.crm.entity.TenantFeatureFlag;
import com.gym.Elite.Gym.crm.repository.TenantFeatureFlagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeatureFlagService {

    private final TenantFeatureFlagRepository repository;

    public boolean isEnabled(UUID tenantId, TenantFeatureFlag.FeatureName featureName) {
        return repository.findByTenantIdAndFeatureName(tenantId, featureName)
                .map(TenantFeatureFlag::isEnabled)
                .orElse(false);
    }
}
