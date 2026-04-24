package com.gym.Elite.Gym.webManagement.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.tenants.entity.TenantRef;
import com.gym.Elite.Gym.tenants.repo.TenantRefRepository;
import com.gym.Elite.Gym.webManagement.dto.galleryDto.GalleryAssetRequestDTO;
import com.gym.Elite.Gym.webManagement.dto.galleryDto.GalleryAssetResponseDTO;
import com.gym.Elite.Gym.webManagement.entity.GalleryAsset;
import com.gym.Elite.Gym.webManagement.mapper.GalleryMapper;
import com.gym.Elite.Gym.webManagement.repo.GalleryAssetRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GalleryService {

    private final GalleryAssetRepo repo;
    private final GalleryMapper galleryMapper;
    private final TenantRefRepository tenantRefRepository;

    // ✅ CREATE (after Cloudinary upload)
    public ResponseDto create(UUID tenantId, GalleryAssetRequestDTO dto) {

        TenantRef tenant = tenantRefRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        GalleryAsset asset = GalleryAsset.builder()
                .imageUrl(dto.getImageUrl())
                .publicId(dto.getPublicId())
                .title(dto.getTitle())
                .altText(dto.getAltText())
                .category(dto.getCategory())
                .isVisible(Boolean.TRUE.equals(dto.getIsVisible()))
                .displayOrder(dto.getDisplayOrder())
                .createdAt(new Date())
                .tenantId(tenantId)
                .build();

        repo.save(asset);

        return ResponseDto.builder()
                .code(201)
                .message("Image added to gallery")
                .build();
    }

    // ✅ GET ALL (ADMIN)
    public List<GalleryAssetResponseDTO> getAll(UUID tenantId) {
        return repo.findByTenantId(tenantId)
                .stream()
                .map(galleryMapper::mapToGalleryDTO)
                .collect(Collectors.toList());
    }

    // ✅ WEBSITE
    public List<GalleryAssetResponseDTO> getVisibleAssets(UUID tenantId) {
        return repo.findByTenantIdAndIsVisibleTrueOrderByDisplayOrderAsc(tenantId)
                .stream()
                .map(galleryMapper::mapToGalleryDTO)
                .collect(Collectors.toList());
    }

    // ✅ UPDATE
    public ResponseDto update(UUID id, GalleryAssetRequestDTO dto) {

        GalleryAsset asset = getEntity(id);

        asset.setTitle(dto.getTitle());
        asset.setAltText(dto.getAltText());
        asset.setCategory(dto.getCategory());
        asset.setIsVisible(dto.getIsVisible());
        asset.setDisplayOrder(dto.getDisplayOrder());
        asset.setUpdatedAt(new Date());

        repo.save(asset);

        return ResponseDto.builder()
                .code(200)
                .message("Gallery updated")
                .build();
    }

    // ✅ DELETE (also delete from Cloudinary later)
    public ResponseDto delete(UUID id) {

        GalleryAsset asset = getEntity(id);

        // TODO: delete from Cloudinary using publicId

        repo.delete(asset);

        return ResponseDto.builder()
                .code(200)
                .message("Deleted successfully")
                .build();
    }

    // ✅ TOGGLE VISIBILITY
    public ResponseDto toggleVisibility(UUID id) {

        GalleryAsset asset = getEntity(id);

        asset.setIsVisible(!asset.getIsVisible());
        repo.save(asset);

        return ResponseDto.builder()
                .code(200)
                .message("Visibility updated")
                .build();
    }

    // 🔁 Helper
    private GalleryAsset getEntity(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
    }

}
