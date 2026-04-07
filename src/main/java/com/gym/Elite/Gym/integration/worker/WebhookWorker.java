package com.gym.Elite.Gym.integration.worker;

import com.gym.Elite.Gym.integration.entity.EventStatus;
import com.gym.Elite.Gym.integration.entity.WebhookEvent;
import com.gym.Elite.Gym.integration.processor.EventProcessor;
import com.gym.Elite.Gym.integration.queue.QueueService;
import com.gym.Elite.Gym.integration.repo.WebhookEventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class WebhookWorker {

    private final QueueService queueService;
    private final WebhookEventRepo repository;

    private final Map<String, EventProcessor> processorMap;

    public WebhookWorker(QueueService queueService, WebhookEventRepo repository, List<EventProcessor> processors) {
        this.queueService = queueService;
        this.repository = repository;
        this.processorMap = processors.stream()
                .collect(Collectors.toMap(EventProcessor::getProvider, p -> p));
    }

    @Scheduled(fixedDelay = 2000)
    public void process() {

        UUID eventId = UUID.fromString(queueService.pop("webhook-events"));

        if (eventId == null) return;

        WebhookEvent event = repository.findById(eventId).orElse(null);

        if (event == null) return;

        try {
            event.setStatus(EventStatus.PROCESSING);

            EventProcessor processor = processorMap.get(event.getSource());

            processor.process(event);

            event.setStatus(EventStatus.DONE);

            event.setProcessedAt(LocalDateTime.now());

        } catch (Exception e) {
            event.setStatus(EventStatus.FAILED);
            event.setRetryCount(event.getRetryCount() + 1);
        }

        repository.save(event);
    }
}
