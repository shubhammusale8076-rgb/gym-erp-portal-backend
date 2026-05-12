package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.seo.SeoSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeoSettingsRepository extends JpaRepository<SeoSettings, UUID> {
    List<SeoSettings> findByTenantId(UUID tenantId);
    Optional<SeoSettings> findByTenantIdAndPageName(UUID tenantId, String pageName);
}
