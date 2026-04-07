package com.gym.Elite.Gym.integration.parser;

public interface WebhookParser {

    String getProvider(); // RAZORPAY, STRIPE

    String extractEventId(String payload);

    String extractTenantId(String payload);

    String extractEventType(String payload);
}
