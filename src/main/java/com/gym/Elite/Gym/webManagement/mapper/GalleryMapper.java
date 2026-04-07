package com.gym.Elite.Gym.webManagement.mapper;

import com.gym.Elite.Gym.webManagement.dto.galleryDto.GalleryAssetResponseDTO;
import com.gym.Elite.Gym.webManagement.entity.GalleryAsset;
import org.springframework.stereotype.Component;

@Component
public class GalleryMapper {

    public GalleryAssetResponseDTO mapToGalleryDTO(GalleryAsset a) {
        return GalleryAssetResponseDTO.builder()
                .id(a.getId())
                .imageUrl(a.getImageUrl())
                .title(a.getTitle())
                .altText(a.getAltText())
                .category(a.getCategory())
                .isVisible(a.getIsVisible())
                .displayOrder(a.getDisplayOrder())
                .build();
    }
}
