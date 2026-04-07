package com.gym.Elite.Gym.integration.processor.razorpay;

import com.gym.Elite.Gym.integration.entity.WebhookEvent;
import com.gym.Elite.Gym.integration.processor.EventProcessor;
import com.gym.Elite.Gym.integration.service.whatsapp.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RazorpayEventProcessor implements EventProcessor {

    @Autowired
    private WhatsAppService whatsAppService;

    @Override
    public String getProvider() {
        return "RAZORPAY";
    }

    @Override
    public void process(WebhookEvent event) {

        String type = event.getEventType();

        if ("payment.captured".equals(type)) {

            String phone = "+919876543210";

            whatsAppService.sendMessage(
                    event.getTenant(),
                    phone,
                    "✅ Payment received! Welcome to the gym 💪"
            );

        }
    }
}
