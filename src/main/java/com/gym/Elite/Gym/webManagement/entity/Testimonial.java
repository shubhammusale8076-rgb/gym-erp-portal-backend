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
@Table(name = "testimonials")
public class Testimonial extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    private String clientName;
    private String role;

    private String profileImageUrl;

    private Integer rating; // 1 to 5

    @Column(length = 2000)
    private String review;

    private String source; // OFFLINE / ONLINE / GOOGLE

    private boolean isApproved;

    private boolean isPublished;

    private boolean isFeatured;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
