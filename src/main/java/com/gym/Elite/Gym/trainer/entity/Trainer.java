package com.gym.Elite.Gym.trainer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "trainers")
public class Trainer {

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
    private List<String> skills; // Yoga, HIIT, etc.

    private String certifications; // comma-separated or JSON later

    // 🔹 SOCIAL LINKS
    private String instagramHandle;
    private String linkedinUrl;

    // 🔹 AVAILABILITY (for UI like your screenshot)
    @ElementCollection
    @CollectionTable(name = "trainer_available_days", joinColumns = @JoinColumn(name = "trainer_id"))
    @Column(name = "day")
    private List<String> availableDays; // MON, TUE, etc.

    private String morningShiftStart;
    private String morningShiftEnd;

    private String eveningShiftStart;
    private String eveningShiftEnd;

    // 🔹 STATUS CONTROL (IMPORTANT)
    private Boolean available; // accepting clients (CRM)
    private Boolean active; // internal enable/disable

    private Boolean visibleOnWebsite; // 🔥 website toggle
    private Boolean featured; // highlight on homepage

    // 🔹 AUDIT
    private Date createdOn;
    private Date updatedOn;

    // 🔹 MULTI-TENANT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenants tenant;
}