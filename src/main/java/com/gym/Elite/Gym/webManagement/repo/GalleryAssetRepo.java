package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.GalleryAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GalleryAssetRepo extends JpaRepository<GalleryAsset, UUID> {

    List<GalleryAsset> findByTenant_Id(UUID tenantId);

    List<GalleryAsset> findByTenant_IdAndIsVisibleTrueOrderByDisplayOrderAsc(UUID tenantId);

    List<GalleryAsset> findByTenant_IdAndCategory(UUID tenantId, String category);
}
