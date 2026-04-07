package com.gym.Elite.Gym.integration.processor;

import com.gym.Elite.Gym.integration.entity.WebhookEvent;

public interface EventProcessor {
    String getProvider();
    void process(WebhookEvent event);
}
