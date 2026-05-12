package com.gym.Elite.Gym.crm.automation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "crm_automation_workflows")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutomationWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Enumerated(EnumType.STRING)
    private TriggerType triggerType;

    @Column(columnDefinition = "TEXT")
    private String conditions; // JSON logic or SpEL expression

    private boolean enabled;
    private java.util.UUID tenantId;
    
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("executionOrder ASC")
    private List<AutomationAction> actions;

    private LocalDateTime createdAt;

    public enum TriggerType {
        LEAD_CREATED, LEAD_ASSIGNED, FOLLOW_UP_OVERDUE, TRIAL_SCHEDULED, LEAD_CONVERTED
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
