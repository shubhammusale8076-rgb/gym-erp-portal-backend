package com.gym.Elite.Gym.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment_items")
public class PaymentItem {

    @Id
    @GeneratedValue
    private UUID id;

    private String name; // Monthly Membership
    private String description; // Billing cycle: Oct 2023

    private Double amount;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    @JsonIgnore
    private Payment payment;
}