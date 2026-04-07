package com.gym.Elite.Gym.webManagement.dto.galleryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalleryAssetRequestDTO {

    private String imageUrl;
    private String publicId;
    private String title;
    private String altText;
    private String category;
    private Boolean isVisible;
    private Integer displayOrder;
}