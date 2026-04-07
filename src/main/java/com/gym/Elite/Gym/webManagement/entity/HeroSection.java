package com.gym.Elite.Gym.webManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hero_section")
public class HeroSection {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID gymId;

    @Column(columnDefinition = "TEXT")
    private String headline;

    @Column(columnDefinition = "TEXT")
    private String subtext;

    private String ctaLabel;
    private String accentPhrase;

    private String backgroundImage;

    private String status; // DRAFT / PUBLISHED

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenants tenant;
}
