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
@Table(name = "testimonials")
public class Testimonial {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenants tenant;

    private String clientName;
    private String role; // e.g. Premium Member

    private String profileImageUrl;

    private Integer rating; // 1 to 5

    @Column(length = 2000)
    private String review;

    private String source; // OFFLINE / ONLINE / GOOGLE

    private boolean isApproved; // admin approved or not

    private boolean isPublished; // visible on website

    private boolean isFeatured; // highlight on homepage

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
