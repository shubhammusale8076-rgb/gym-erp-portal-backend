package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.SocialLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLink, UUID> {
    List<SocialLink> findByTenantIdOrderByDisplayOrderAsc(UUID tenantId);
    List<SocialLink> findByTenantIdAndActiveTrueOrderByDisplayOrderAsc(UUID tenantId);
}
