package com.gym.Elite.Gym.webManagement.entity;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "hero_section")
public class HeroSection extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;


    @Column(columnDefinition = "TEXT")
    private String headline;

    @Column(columnDefinition = "TEXT")
    private String subtext;

    private String ctaLabel;
    private String accentPhrase;

    private String backgroundImage;

    private String status; // DRAFT / PUBLISHED

    private LocalDateTime updatedAt;
}
