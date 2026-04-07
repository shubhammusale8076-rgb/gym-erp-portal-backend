package com.gym.Elite.Gym.auth.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    @JsonIgnore
    private Tenants tenant;

    @ElementCollection
    @CollectionTable(name = "membership_plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    @OneToMany(mappedBy = "plan" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberSubscription> subscriptions = new ArrayList<>();

}
