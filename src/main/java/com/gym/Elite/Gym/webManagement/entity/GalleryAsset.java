package com.gym.Elite.Gym.webManagement.entity;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "gallery_assets")
public class GalleryAsset extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    private String imageUrl;
    private String publicId;

    private String title;
    private String altText;

    private String category; // INTERIOR, EQUIPMENT, STAFF

    private Boolean isVisible;
    private Integer displayOrder;

    private Date createdAt;
    private Date updatedAt;

}