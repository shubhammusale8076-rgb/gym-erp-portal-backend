package com.gym.Elite.Gym.integration.controller;

import com.gym.Elite.Gym.integration.service.WebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/{provider}")
    public ResponseEntity<String> handleWebhook(
            @PathVariable String provider,
            @RequestHeader Map<String, String> headers,
            @RequestBody String payload) {

        webhookService.handle(provider, headers, payload);

        return ResponseEntity.ok("received");
    }

    @GetMapping("/whatsapp")
    public String verifyWebhook(@RequestParam("hub.challenge") String challenge,
                                @RequestParam("hub.verify_token") String token) {

        if ("your_verify_token".equals(token)) {
            return challenge;
        }
        return "Invalid";
    }
}
