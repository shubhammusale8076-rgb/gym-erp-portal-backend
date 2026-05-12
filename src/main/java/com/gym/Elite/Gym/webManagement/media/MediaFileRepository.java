package com.gym.Elite.Gym.webManagement.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {
    List<MediaFile> findByTenantId(UUID tenantId);
    List<MediaFile> findByTenantIdAndCategory(UUID tenantId, String category);
}
