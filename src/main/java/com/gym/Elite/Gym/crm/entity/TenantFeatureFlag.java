package com.gym.Elite.Gym.crm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "crm_tenant_feature_flags")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantFeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    private FeatureName featureName;

    private boolean enabled;

    public enum FeatureName {
        WHATSAPP_AUTOMATION, 
        AUTO_ASSIGNMENT, 
        GOOGLE_CALENDAR_SYNC, 
        FOLLOWUP_AUTOMATION
    }
}
