package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.GalleryAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GalleryAssetRepo extends JpaRepository<GalleryAsset, UUID> {

    List<GalleryAsset> findByTenantId(UUID tenantId);

    List<GalleryAsset> findByTenantIdAndIsVisibleTrueOrderByDisplayOrderAsc(UUID tenantId);

    List<GalleryAsset> findByTenantIdAndCategory(UUID tenantId, String category);
}
