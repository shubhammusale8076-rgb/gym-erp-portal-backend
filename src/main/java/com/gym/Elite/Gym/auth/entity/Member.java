package com.gym.Elite.Gym.auth.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.payment.entity.Payment;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;

    @JsonIgnore
    private String password;

    private Date createdOn;

    private Date updatedOn;

    @Column(nullable = false,unique = true)
    private String email;

    private String phoneNumber;

    private Boolean active;

    private String emergencyContact;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    @JsonIgnore
    private Tenants tenant;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberSubscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments= new ArrayList<>();

}
