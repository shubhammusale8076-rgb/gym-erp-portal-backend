package com.gym.Elite.Gym.integration.processor.razorpay;

import com.gym.Elite.Gym.integration.client.IntegrationClient;
import com.gym.Elite.Gym.integration.entity.WebhookEvent;
import com.gym.Elite.Gym.integration.processor.EventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RazorpayEventProcessor implements EventProcessor {

    @Autowired
    private IntegrationClient integrationClient;

    @Override
    public String getProvider() {
        return "RAZORPAY";
    }

    @Override
    public void process(WebhookEvent event) {

        String type = event.getEventType();

        if ("payment.captured".equals(type)) {

            integrationClient.sendEvent(
                    "PAYMENT_CAPTURED",
                    event.getTenant().getId().toString(),
                    event
            );

        }
    }
}
