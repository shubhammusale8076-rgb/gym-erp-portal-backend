package com.gym.Elite.Gym.trainer.entity;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "trainers")
public class Trainer extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    // 🔹 BASIC INFO (CRM)
    private String fullName;
    private String email;
    private String phoneNumber;

    private Integer experienceInYears;

    // 🔹 WEBSITE PROFILE
    private String bio;
    private String profileImageUrl;

    @ElementCollection
    @CollectionTable(name = "trainer_skills", joinColumns = @JoinColumn(name = "trainer_id"))
    @Column(name = "skill")
    private List<String> skills;

    private String certifications;

    // 🔹 SOCIAL LINKS
    private String instagramHandle;
    private String linkedinUrl;

    // 🔹 AVAILABILITY
    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainerAvailability> availability = new ArrayList<>();

    // 🔹 STATUS CONTROL
    private Boolean available;
    private Boolean active;
    private Boolean visibleOnWebsite;
    private Boolean featured;

    // 🔹 AUDIT
    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<TrainerMemberAssignment> assignments = new ArrayList<>();


    // ✅ Multi-tenant: plain column, no FK constraint
}