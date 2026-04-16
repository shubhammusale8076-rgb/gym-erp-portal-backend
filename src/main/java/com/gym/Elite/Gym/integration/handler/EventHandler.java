package com.gym.Elite.Gym.integration.handler;

import com.gym.Elite.Gym.integration.dto.EventRequest;

public interface EventHandler {
    void handle(EventRequest event);
    String getEventType();
}
