package com.gym.Elite.Gym.webManagement.feature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantFeatureRepository extends JpaRepository<TenantFeature, UUID> {
    Optional<TenantFeature> findByTenantIdAndFeatureModule(UUID tenantId, FeatureModule featureModule);
}
