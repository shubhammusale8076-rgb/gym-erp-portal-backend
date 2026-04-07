package com.gym.Elite.Gym.integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.integration.config.RazorpayConfig;
import com.gym.Elite.Gym.integration.entity.EventStatus;
import com.gym.Elite.Gym.integration.entity.Integration;
import com.gym.Elite.Gym.integration.entity.WebhookEvent;
import com.gym.Elite.Gym.integration.parser.WebhookParser;
import com.gym.Elite.Gym.integration.parser.WebhookParserFactory;
import com.gym.Elite.Gym.integration.queue.QueueService;
import com.gym.Elite.Gym.integration.repo.IntegrationRepo;
import com.gym.Elite.Gym.integration.repo.WebhookEventRepo;
import com.gym.Elite.Gym.integration.resolver.TenantResolver;
import com.gym.Elite.Gym.integration.resolver.TenantResolverFactory;
import com.gym.Elite.Gym.integration.utill.RazorpaySignatureUtil;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.tenants.repo.TenantRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class WebhookService {

    private final WebhookEventRepo repository;
    private final QueueService queueService;
    private final WebhookParserFactory parserFactory;
    private final IntegrationRepo integrationRepo;
    private final RazorpaySignatureUtil signatureUtil;
    private final TenantResolverFactory tenantResolverFactory;

    public WebhookService(WebhookEventRepo repository,
                          QueueService queueService, TenantRepo tenantRepo, WebhookParserFactory parserFactory, IntegrationRepo integrationRepo, RazorpaySignatureUtil signatureUtil, TenantResolverFactory tenantResolverFactory) {
        this.repository = repository;
        this.queueService = queueService;
        this.parserFactory = parserFactory;
        this.integrationRepo = integrationRepo;
        this.signatureUtil = signatureUtil;
        this.tenantResolverFactory = tenantResolverFactory;
    }

    public void handle(String provider, Map<String, String> headers, String payload) {

        WebhookParser parser = parserFactory.getParser(provider);

        // 1. Extract eventId (provider-specific)
        String externalEventId = parser.extractEventId(payload);

        // 2. Idempotency check
        if (repository.existsByExternalEventId(externalEventId)) {
            return;
        }

        String eventType = parser.extractEventType(payload);

        TenantResolver resolver = tenantResolverFactory.get(provider);
        Tenants tenant = resolver.resolve(payload);

        Integration integration = integrationRepo
                .findByTenantAndProvider(tenant, provider)
                .orElseThrow();

        String secret = extractSecret(integration.getConfigJson());

        if ("razorpay".equalsIgnoreCase(provider)) {

            RazorpayConfig config = parseConfig(integration.getConfigJson());

            String signature = headers.get("x-razorpay-signature");

            if (!signatureUtil.verify(payload, signature, config.getWebhookSecret())) {
                throw new RuntimeException("Invalid signature");
            }
        }

        // 3. Save event
        WebhookEvent event = new WebhookEvent();
        event.setTenant(tenant);
        event.setSource(provider.toUpperCase());
        event.setPayload(payload);
        event.setEventType(eventType);
        event.setExternalEventId(externalEventId);
        event.setStatus(EventStatus.PENDING);
        event.setCreatedAt(LocalDateTime.now());

        repository.save(event);

        // 4. Push to Redis queue
        queueService.push("webhook-events", String.valueOf(event.getId()));
    }

    private String extractSecret(String configJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(configJson);

            return node.path("webhookSecret").asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract webhook secret", e);
        }
    }

    private RazorpayConfig parseConfig(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, RazorpayConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Razorpay config", e);
        }
    }
}
