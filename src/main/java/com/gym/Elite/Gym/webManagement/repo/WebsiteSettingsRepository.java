package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.WebsiteSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WebsiteSettingsRepository extends JpaRepository<WebsiteSettings, UUID> {
    Optional<WebsiteSettings> findByTenantId(UUID tenantId);
}
