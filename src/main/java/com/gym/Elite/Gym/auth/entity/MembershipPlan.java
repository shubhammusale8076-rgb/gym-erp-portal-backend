package com.gym.Elite.Gym.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "membership_plan")
public class MembershipPlan {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private Double price;

    private Integer durationInDays; // 30, 90, 365

    private Integer sessionLimit; // null = unlimited

    private Boolean personalTrainerIncluded;

    private Boolean dietPlanIncluded;

    private Double discount; // optional

    private Boolean active;

    // ✅ Multi-tenant: plain column, no FK constraint
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @ElementCollection
    @CollectionTable(
            name = "membership_plan_features",
            joinColumns = @JoinColumn(name = "plan_id", columnDefinition = "uuid")
    )
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberSubscription> subscriptions = new ArrayList<>();
}
