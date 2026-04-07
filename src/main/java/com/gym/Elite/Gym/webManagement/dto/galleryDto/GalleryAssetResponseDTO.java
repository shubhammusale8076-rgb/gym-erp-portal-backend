package com.gym.Elite.Gym.webManagement.dto.galleryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalleryAssetResponseDTO {

    private UUID id;
    private String imageUrl;
    private String title;
    private String altText;
    private String category;
    private Boolean isVisible;
    private Integer displayOrder;
}
