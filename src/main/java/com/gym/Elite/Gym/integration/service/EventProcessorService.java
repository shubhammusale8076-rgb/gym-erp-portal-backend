package com.gym.Elite.Gym.integration.service;

import com.gym.Elite.Gym.integration.dto.EventRequest;
import com.gym.Elite.Gym.integration.handler.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventProcessorService {

    private final Map<String, EventHandler> handlerMap;

    public EventProcessorService(List<EventHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(EventHandler::getEventType, h -> h));
    }

    public void process(EventRequest event) {
        log.info("Processing event: {} for tenant: {}", event.getEventType(), event.getTenantId());

        EventHandler handler = handlerMap.get(event.getEventType());

        if (handler != null) {
            handler.handle(event);
        } else {
            log.warn("No handler found for event type: {}", event.getEventType());
        }
    }
}
