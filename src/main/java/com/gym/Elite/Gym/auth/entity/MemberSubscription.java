package com.gym.Elite.Gym.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "gym_member_subscription")
public class MemberSubscription {

    @Id
    @GeneratedValue
    private UUID id;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private Boolean active;
    private Boolean autoRenew;
    private Integer remainingSessions;

    private Integer durationInDays;
    private Double price;
    private Double discount;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;

    private Boolean frozen = false;

    private LocalDateTime actualUnfreezeDate;

    private LocalDateTime freezeStartDate;

    private LocalDateTime freezeEndDate;

    private Integer totalFreezeDays = 0;

    private String freezingReason;

    private String unFreezingReason;

    private String cancellationReason;

    private LocalDateTime cancelledOn;

    // ✅ Tenant isolation — plain column, no FK constraint
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

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
}
