package com.gym.Elite.Gym.crm.assignment.entity;

import com.gym.Elite.Gym.crm.entity.Lead;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crm_lead_assignment_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadAssignmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    private java.util.UUID previousAssignedToId;
    private String previousAssignedToName;

    private java.util.UUID newAssignedToId;
    private String newAssignedToName;

    private String changedBy;
    private String reason;
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }
}
