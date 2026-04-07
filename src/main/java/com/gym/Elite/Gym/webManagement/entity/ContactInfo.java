package com.gym.Elite.Gym.webManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "contact_info",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "status"})
)
@Getter
@Setter
public class ContactInfo {

    @Id
    @GeneratedValue
    private UUID id;

    // ✅ Multi-tenant mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenants tenant;

    // 📞 Public Presence
    private String supportEmail;
    private String phoneNumber;
    private String emergencyContact;

    @Column(columnDefinition = "TEXT")
    private String address;

    // 🌐 Social Links
    private String instagram;
    private String twitter;
    private String facebook;
    private String youtube;

    // 🧠 CMS Control
    @Column(nullable = false)
    private String status; // DRAFT / PUBLISHED

    private LocalDateTime updatedAt;
}
