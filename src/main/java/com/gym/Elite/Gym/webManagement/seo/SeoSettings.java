package com.gym.Elite.Gym.webManagement.seo;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seo_settings")
public class SeoSettings extends TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String pageName; // HOME, ABOUT, GALLERY, etc. or URL path

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    private String keywords;
    private String canonicalUrl;
    private String ogImage;

    @Column(columnDefinition = "TEXT")
    private String robotsConfig;
}
