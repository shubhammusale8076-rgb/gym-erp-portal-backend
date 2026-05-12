package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.AboutSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AboutSectionRepository extends JpaRepository<AboutSection, UUID> {
    Optional<AboutSection> findByTenantId(UUID tenantId);
}
