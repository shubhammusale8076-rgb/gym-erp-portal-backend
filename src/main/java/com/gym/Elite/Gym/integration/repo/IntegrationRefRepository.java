package com.gym.Elite.Gym.integration.repo;

import com.gym.Elite.Gym.integration.entity.IntegrationRef;
import com.gym.Elite.Gym.integration.entity.IntegrationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntegrationRefRepository extends JpaRepository<IntegrationRef, UUID> {
    List<IntegrationRef> findByTenantId(UUID tenantId);
    Optional<IntegrationRef> findByTenantIdAndIntegrationType(UUID tenantId, IntegrationType type);
}
