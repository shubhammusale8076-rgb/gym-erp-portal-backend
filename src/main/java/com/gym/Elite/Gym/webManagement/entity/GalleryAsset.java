package com.gym.Elite.Gym.webManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "gallery_assets")
public class GalleryAsset {

    @Id
    @GeneratedValue
    private UUID id;

    private String imageUrl;   // Cloudinary URL
    private String publicId;   // Cloudinary public ID

    private String title;
    private String altText;

    private String category; // INTERIOR, EQUIPMENT, STAFF

    private Boolean isVisible;
    private Integer displayOrder;

    private Date createdAt;
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenants tenant;
}