package com.gym.Elite.Gym.crm.automation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "crm_automation_actions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutomationAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workflow_id")
    private AutomationWorkflow workflow;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(columnDefinition = "TEXT")
    private String actionConfig; // JSON config (templateId, assignedToId, etc.)

    private int executionOrder;

    public enum ActionType {
        SEND_WHATSAPP, CREATE_FOLLOWUP, CREATE_CALENDAR_EVENT, ASSIGN_LEAD, NOTIFY_STAFF, UPDATE_STAGE
    }
}
