package com.gym.Elite.Gym.crm.repository;

import com.gym.Elite.Gym.crm.entity.TenantFeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantFeatureFlagRepository extends JpaRepository<TenantFeatureFlag, Long> {
    Optional<TenantFeatureFlag> findByTenantIdAndFeatureName(UUID tenantId, TenantFeatureFlag.FeatureName featureName);
}
