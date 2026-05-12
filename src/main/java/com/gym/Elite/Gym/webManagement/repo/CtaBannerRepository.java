package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.CtaBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CtaBannerRepository extends JpaRepository<CtaBanner, UUID> {
    Optional<CtaBanner> findByTenantId(UUID tenantId);
}
