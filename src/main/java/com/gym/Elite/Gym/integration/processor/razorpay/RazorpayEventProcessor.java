package com.gym.Elite.Gym.integration.processor.razorpay;

import com.gym.Elite.Gym.integration.entity.WebhookEvent;
import com.gym.Elite.Gym.integration.processor.EventProcessor;
import com.gym.Elite.Gym.payment.entity.Payment;
import com.gym.Elite.Gym.payment.entity.ProcessedWebhook;
import com.gym.Elite.Gym.payment.repo.PaymentRepo;
import com.gym.Elite.Gym.payment.repo.ProcessedWebhookRepo;
import com.gym.Elite.Gym.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class RazorpayEventProcessor implements EventProcessor {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private ProcessedWebhookRepo processedWebhookRepo;

    @Value("${razorpay.webhook.secret:secret}")
    private String webhookSecret;

    @Override
    public String getProvider() {
        return "RAZORPAY";
    }

    @Override
    @Transactional
    public void process(WebhookEvent event) {
        // 1. Idempotency Check (DB-level)
        if (event.getProviderEventId() == null || processedWebhookRepo.existsById(event.getProviderEventId())) {
            log.warn("Webhook event {} already processed or invalid ID", event.getProviderEventId());
            return;
        }

        // 2. Mandatory Signature Verification
        if (!verifySignature(event)) {
            log.error("Invalid signature for event {}", event.getProviderEventId());
            throw new RuntimeException("Invalid webhook signature");
        }

        // 3. Explicit Event Type Validation
        String type = event.getEventType();
        if (!"payment.captured".equals(type)) {
            log.info("Ignoring event type: {}", type);
            return;
        }

        Map<String, Object> payload = (Map<String, Object>) event.getPayload();
        Map<String, Object> paymentEntity = (Map<String, Object>) ((Map<String, Object>) payload.get("payment")).get("entity");
        Map<String, Object> notes = (Map<String, Object>) paymentEntity.get("notes");

        if (notes != null && notes.containsKey("payment_id")) {
            UUID internalPaymentId = UUID.fromString((String) notes.get("payment_id"));
            String razorpayPaymentId = (String) paymentEntity.get("id");
            Integer amount = (Integer) paymentEntity.get("amount"); // Amount in paise

            // 4. Cross-check with DB
            Payment payment = paymentRepo.findById(internalPaymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found in DB"));

            // Verify amount (paise to rupees conversion)
            if (Math.abs(payment.getTotalAmount() * 100 - amount) > 0.01) {
                log.error("Amount mismatch for payment {}: expected {}, got {}",
                        internalPaymentId, payment.getTotalAmount() * 100, amount);
                throw new RuntimeException("Payment amount mismatch");
            }

            paymentService.confirmPayment(internalPaymentId, razorpayPaymentId);
        }

        // 5. Mark as processed (Transaction will commit this)
        processedWebhookRepo.save(new ProcessedWebhook(event.getProviderEventId(), new Date()));
    }

    private boolean verifySignature(WebhookEvent event) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(event.getRawBody().getBytes(StandardCharsets.UTF_8));
            String expectedSignature = HexFormat.of().formatHex(hash);

            return expectedSignature.equals(event.getSignature());
        } catch (Exception e) {
            log.error("Signature verification error", e);
            return false;
        }
    }
}
