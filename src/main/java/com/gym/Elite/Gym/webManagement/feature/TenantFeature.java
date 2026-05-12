package com.gym.Elite.Gym.webManagement.feature;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tenant_features", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "feature_module"})
})
public class TenantFeature extends TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "feature_module", nullable = false)
    private FeatureModule featureModule;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;
}
