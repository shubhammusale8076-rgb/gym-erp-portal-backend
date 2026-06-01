package com.gym.Elite.Gym.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.payment.entity.Payment;
import com.gym.Elite.Gym.common.entity.TenantAware;
import com.gym.Elite.Gym.trainer.entity.TrainerMemberAssignment;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(
        name = "gym_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"tenant_id", "email"})
        }
)
public class Member extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;

    @Column(nullable = false)
    private String email;

    private String address;

    private String profileImageUrl;

    private String profileImagePublicId;

    private String phoneNumber;

    private Boolean active;

    private String emergencyContactName;

    private String emergencyContactNumber;

    @Column(name = "aadhaar_number", columnDefinition = "TEXT")
    private String aadhaarNumber;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MemberSubscription> subscriptions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<TrainerMemberAssignment> trainerAssignments;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private GymUser gymUser;
}
