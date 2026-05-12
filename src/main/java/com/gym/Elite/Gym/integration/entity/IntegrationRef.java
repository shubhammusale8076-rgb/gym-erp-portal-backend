package com.gym.Elite.Gym.integration.entity;

import com.gym.Elite.Gym.integration.dto.IntegrationAuthType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "integration_ref",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "tenantId",
                                "integrationType"
                        }
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationRef {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntegrationType integrationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntegrationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type")
    private IntegrationAuthType authType;

    private boolean connected;

    private LocalDateTime lastSyncedAt;

    private LocalDateTime lastConnectionAt;

    private LocalDateTime lastFailureAt;

    @Column(columnDefinition = "TEXT")
    private String lastError;
}
