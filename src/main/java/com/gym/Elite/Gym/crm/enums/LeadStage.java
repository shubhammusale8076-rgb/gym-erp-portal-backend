package com.gym.Elite.Gym.crm.enums;

public enum LeadStage {
    NEW_LEAD(1),
    CONTACTED(2),
    FOLLOW_UP(3),
    TRIAL_SCHEDULED(4),
    NEGOTIATION(5),
    CONVERTED(6),
    LOST(7);

    private final int order;

    LeadStage(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
