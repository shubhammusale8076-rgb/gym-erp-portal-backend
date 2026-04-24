package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.HeroSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HeroSectionRepo extends JpaRepository<HeroSection, UUID> {

    Optional<HeroSection> findByTenantId(UUID tenantId);
    Optional<HeroSection> findByTenantIdAndStatus(UUID tenantId, String status);
}
