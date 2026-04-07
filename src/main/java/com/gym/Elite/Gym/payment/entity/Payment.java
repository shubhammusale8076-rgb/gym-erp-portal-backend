package com.gym.Elite.Gym.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    private String transactionReference; // TRX-88291

    private Double subtotal;
    private Double taxAmount;
    private Double discountAmount;
    private Double totalAmount;

    private String currency; // INR

    private String status; // PAID, FAILED, PENDING

    private String paymentMethod; // CARD, UPI, CASH

    private String cardLast4; // 4242

    private Date paymentDate;

    private UUID planId;

    private UUID subscriptionId; // nullable (for renewals)

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    @JsonIgnore
    private Tenants tenant;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentItem> items = new ArrayList<>();
}