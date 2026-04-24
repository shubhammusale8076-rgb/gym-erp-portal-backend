package com.gym.Elite.Gym.webManagement.entity;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "contact_info",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "status"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ContactInfo extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    private String supportEmail;
    private String phoneNumber;
    private String emergencyContact;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String instagram;
    private String twitter;
    private String facebook;
    private String youtube;

    @Column(nullable = false)
    private String status; // DRAFT / PUBLISHED

    private LocalDateTime updatedAt;
}
