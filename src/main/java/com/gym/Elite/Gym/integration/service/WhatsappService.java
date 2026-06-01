package com.gym.Elite.Gym.integration.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.entity.MemberSubscription;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.auth.repo.SubscriptionPlanRepo;
import com.gym.Elite.Gym.crm.integration.client.WhatsAppIntegrationClient;
import com.gym.Elite.Gym.integration.dto.WhatsAppDeliveryStatus;
import com.gym.Elite.Gym.integration.dto.whatsapp.WelcomeMessage;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WhatsappService {

    private final MemberRepo memberRepo;
    private final SubscriptionPlanRepo subscriptionRepo;
    private final WhatsAppIntegrationClient integrationClient;

    public ResponseDto sendWelcomeMsg(UUID memberId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Member member = memberRepo.findByIdAndTenantId(memberId, tenantId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (member.getPhoneNumber() == null || member.getPhoneNumber().isBlank()) {

            return ResponseDto.builder()
                    .code(400)
                    .message("Member phone number missing")
                    .build();
        }

        MemberSubscription subscription = subscriptionRepo
                .findTopByMemberIdAndTenantIdOrderByCreatedOnDesc(memberId, tenantId)
                .orElseThrow(() -> new RuntimeException("Active subscription not found"));

        String correlationId = MDC.get("correlationId");

        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        String trainerName = member.getTrainerAssignments() != null
                ? member.getTrainerAssignments().stream()
                .filter(a -> Boolean.TRUE.equals(a.getActive()))
                .findFirst()
                .map(a -> a.getTrainer().getFullName())
                .orElse("Self")
                : "Self";


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

        WelcomeMessage welcomeMessage = WelcomeMessage.builder()
                .tenantId(tenantId)
                .correlationId(correlationId)
                .memberName(member.getFullName())
                .planName(subscription.getPlan().getName())
                .phoneNumber(member.getPhoneNumber())
                .trainerName(trainerName)
                .planStartDate(subscription.getStartDate().format(formatter).toUpperCase())
                .build();


        try{
            WhatsAppDeliveryStatus response = integrationClient.sendWelcomeMessage(correlationId,welcomeMessage);
            log.info(
                    "Welcome WhatsApp sent successfully memberId={} correlationId={} status={}",
                    memberId,
                    correlationId,
                    response
            );

            return ResponseDto.builder()
                      .code(200)
                      .message("Welcome Message Delivered")
                      .build();

        } catch (Exception ex) {
            log.error(
                    "Failed to send welcome WhatsApp memberId={} correlationId={} error={}",
                    memberId,
                    correlationId,
                    ex.getMessage(),
                    ex
            );
            return ResponseDto.builder()
                    .code(400)
                    .message(ex.getMessage())
                    .build();

        }finally {

            MDC.remove("correlationId");
        }

    }
}
