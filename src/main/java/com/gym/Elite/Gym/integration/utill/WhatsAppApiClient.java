package com.gym.Elite.Gym.integration.utill;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class WhatsAppApiClient {

    public void sendMessage(String phoneNumberId,
                            String accessToken,
                            String to,
                            String message) {

        String url = "https://graph.facebook.com/v18.0/"
                + phoneNumberId + "/messages";

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "text",
                "text", Map.of("body", message)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        new RestTemplate().postForEntity(url, request, String.class);
    }
}