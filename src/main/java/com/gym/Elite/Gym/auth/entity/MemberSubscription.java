package com.gym.Elite.Gym.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.payment.entity.Payment;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member_subscription")
public class MemberSubscription {

    @Id
    @GeneratedValue
    private UUID id;

    private Date startDate;
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private Boolean active;
    private Boolean autoRenew;
    private Integer remainingSessions;
    private Date createdOn;
    private Date updatedOn;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    @JsonIgnore
    private MembershipPlan plan;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    @JsonIgnore
    private Tenants tenant;
}
