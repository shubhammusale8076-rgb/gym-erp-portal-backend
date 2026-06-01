package com.gym.Elite.Gym.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(
        name = "gym_users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"tenant_id", "email"})
        }
)
public class GymUser extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;

    @JsonIgnore
    private String password;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @UpdateTimestamp
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    private Integer tokenVersion = 0;

    @Builder.Default
    @Column(name = "enabled")
    private boolean enabled = false;

    @Column(name = "password_reset_required")
    @Builder.Default
    private Boolean passwordResetRequired = false;

    @Column(name = "password_updated_at")
    private LocalDateTime passwordUpdatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_id")
    private Role role;

    public boolean isActive() {
        return enabled;
    }

    public void setActive(boolean active) {
        this.enabled = active;
    }
}
