package com.gym.Elite.Gym.trainer.entity;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "trainer_leaves")
public class TrainerLeave extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "trainer_id", nullable = false)
    private UUID trainerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    private UUID approvedBy;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
