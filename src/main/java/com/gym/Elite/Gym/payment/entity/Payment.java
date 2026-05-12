package com.gym.Elite.Gym.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "gym_payments")
public class Payment extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    private String transactionReference;

    private Double subtotal;
    private Double taxAmount;
    private Double discountAmount;
    private Double totalAmount;

    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String paymentMethod;
    private String cardLast4;
    private LocalDateTime paymentDate;


    private UUID planId;
    private UUID subscriptionId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;
}

