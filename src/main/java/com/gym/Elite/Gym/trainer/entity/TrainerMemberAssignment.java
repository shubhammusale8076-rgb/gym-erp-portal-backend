package com.gym.Elite.Gym.trainer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.auth.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "trainer_member_assignment")
public class TrainerMemberAssignment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    @JsonIgnore
    private Trainer trainer;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    // 🔥 IMPORTANT (future use)
    private LocalDateTime assignedOn;

    private LocalDateTime endDate;

    private Boolean active;

    private String goal;          // weight loss / muscle gain
    private String notes;         // trainer notes
    private Integer sessionsPerWeek;

    @CreationTimestamp
    private LocalDateTime createdOn;
}
