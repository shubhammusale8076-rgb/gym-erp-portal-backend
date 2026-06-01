package com.gym.Elite.Gym.payment.service;

import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.entity.MemberSubscription;
import com.gym.Elite.Gym.auth.entity.SubscriptionStatus;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.auth.repo.SubscriptionPlanRepo;
import com.gym.Elite.Gym.crm.integration.client.WhatsAppIntegrationClient;
import com.gym.Elite.Gym.crm.integration.client.EmailIntegrationClient;
import com.gym.Elite.Gym.crm.integration.dto.WhatsAppRequest;
import com.gym.Elite.Gym.crm.integration.dto.EmailRequest;
import com.gym.Elite.Gym.crm.integration.dto.EventResponse;
import com.gym.Elite.Gym.integration.client.IntegrationClient;
import com.gym.Elite.Gym.integration.dto.PaymentLinkRequest;
import com.gym.Elite.Gym.integration.dto.PaymentLinkResponse;
import com.gym.Elite.Gym.payment.dto.PaymentTransactionResponseDTO;
import com.gym.Elite.Gym.payment.entity.Payment;
import com.gym.Elite.Gym.payment.entity.PaymentStatus;
import com.gym.Elite.Gym.payment.entity.PaymentTransaction;
import com.gym.Elite.Gym.payment.entity.SyncStatus;
import com.gym.Elite.Gym.payment.repo.PaymentRepo;
import com.gym.Elite.Gym.payment.repo.PaymentTransactionRepo;
import com.gym.Elite.Gym.internal.dto.PaymentConfirmRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTransactionService {

    private final PaymentTransactionRepo transactionRepo;
    private final PaymentRepo paymentRepo;
    private final MemberRepo memberRepo;
    private final SubscriptionPlanRepo subscriptionRepo;
    private final IntegrationClient integrationClient;
    private final WhatsAppIntegrationClient whatsappClient;
    private final EmailIntegrationClient emailClient;

    @Transactional
    public PaymentTransaction createPaymentTransaction(Member member, MemberSubscription subscription) {
        log.info("Creating PaymentTransaction for member ID: {} and subscription ID: {}", member.getId(), subscription.getId());
        PaymentTransaction transaction = PaymentTransaction.builder()
                .memberId(member.getId())
                .membershipId(subscription.getId())
                .amount(subscription.getPrice())
                .paymentStatus(PaymentStatus.PENDING)
                .syncStatus(SyncStatus.PENDING)
                .tenantId(member.getTenantId())
                .build();
        return transactionRepo.save(transaction);
    }

    public PaymentLinkResponse  syncPaymentTransaction(PaymentTransaction transaction, Member member, MemberSubscription subscription) {
        log.info("Attempting to sync payment transaction ID: {} with Integration Service", transaction.getId());

        String correlationId = MDC.get("correlationId");

        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        PaymentLinkRequest request = PaymentLinkRequest.builder()
                .tenantId(transaction.getTenantId())
                .transactionId(transaction.getId().toString())
                .amount(transaction.getAmount())
                .correlationId(correlationId)
                .memberId(member.getId())
                .memberName(member.getFullName())
                .email(member.getEmail())
                .phone(member.getPhoneNumber())
                .membershipId(subscription.getId())
                .durationDays(subscription.getDurationInDays())

                .planId(subscription.getPlan().getId())
                .planName(subscription.getPlan().getName())

                .build();

        try {
            PaymentLinkResponse response = integrationClient.createPaymentLink(correlationId, request);

            if (response != null && response.getRazorpayOrderId() != null && !response.getRazorpayOrderId().isBlank()) {
                transaction.setPaymentLink(response.getUniversalPaymentLink());
                transaction.setRazorpayPaymentLinkId(response.getRazorpayOrderId());
                transaction.setSyncStatus(SyncStatus.SUCCESS);
                log.info("Successfully synced payment link for transaction ID: {}", transaction.getId());
            }
            else {
                transaction.setSyncStatus(SyncStatus.FAILED);
                transaction.setRetryCount(transaction.getRetryCount() + 1);
                log.warn("Integration service returned empty payment link for transaction ID: {}", transaction.getId());
            }

            transactionRepo.save(transaction);

            return response;
        } catch (Exception e) {
            transaction.setSyncStatus(SyncStatus.FAILED);
            transaction.setRetryCount(transaction.getRetryCount() + 1);
            transactionRepo.save(transaction);
            log.error("Failed to sync payment transaction ID: {} with Integration Service. Error: {}", transaction.getId(), e.getMessage());

            return PaymentLinkResponse.builder()
                    .transactionId(transaction.getId().toString())
                    .correlationId(correlationId)
                    .universalPaymentLink(null)
                    .razorpayOrderId(null)
                    .whatsappError(e.getMessage())
                    .build();
        }



    }

    @Transactional
    public void confirmPayment(PaymentConfirmRequest request) {
        log.info("Received payment confirmation webhook. Link ID: {}, Payment ID: {}, Tenant: {}",
                request.getRazorpayPaymentLinkId(), request.getPaymentId(), request.getTenantId());

        PaymentTransaction transaction = transactionRepo.findByRazorpayPaymentLinkId(request.getRazorpayPaymentLinkId())
                .orElseThrow(() -> new RuntimeException("Payment transaction not found for link ID: " + request.getRazorpayPaymentLinkId()));

        if (PaymentStatus.SUCCESS.equals(transaction.getPaymentStatus())) {
            log.info("Payment transaction ID: {} is already PAID. Skipping activation.", transaction.getId());
            return;
        }

        // 1. Mark transaction as PAID
        transaction.setPaymentStatus(PaymentStatus.SUCCESS);
        transaction.setPaymentId(request.getPaymentId());
        transactionRepo.save(transaction);

        // 2. Activate subscription
        MemberSubscription subscription = subscriptionRepo.findById(transaction.getMembershipId())
                .orElseThrow(() -> new RuntimeException("Subscription not found for ID: " + transaction.getMembershipId()));

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(subscription.getDurationInDays());

        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setActive(true);

        // 3. Create and Save ledger Payment record
        Payment ledgerPayment = Payment.builder()
                .transactionReference(request.getPaymentId())
                .subtotal(transaction.getAmount())
                .taxAmount(0.0)
                .discountAmount(0.0)
                .totalAmount(transaction.getAmount())
                .currency("INR")
                .status(PaymentStatus.SUCCESS)
                .paymentMethod("Razorpay")
                .paymentDate(LocalDateTime.now())
                .planId(subscription.getPlan().getId())
                .subscriptionId(subscription.getId())
                .tenantId(transaction.getTenantId())
                .build();

        // Retrieve member and associate
        Member member = memberRepo.findById(transaction.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found for ID: " + transaction.getMemberId()));
        ledgerPayment.setMember(member);
        Payment savedLedgerPayment = paymentRepo.save(ledgerPayment);

        subscription.setPayment(savedLedgerPayment);
        subscriptionRepo.save(subscription);

        // 4. Activate member account
        member.setActive(true);
        memberRepo.save(member);

        log.info("Successfully activated member ID: {} and subscription ID: {} following payment: {}",
                member.getId(), subscription.getId(), request.getPaymentId());

        // 5. Post-Payment Notifications (Async triggers in try-catch to keep core transactional safety)
        triggerPostPaymentNotifications(member, subscription, savedLedgerPayment);
    }

    private void triggerPostPaymentNotifications(Member member, MemberSubscription subscription, Payment payment) {
        String correlationId = MDC.get("correlationId");
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        // WhatsApp notification
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("memberName", member.getFullName());
            variables.put("planName", subscription.getPlan().getName());
            variables.put("amount", String.valueOf(payment.getTotalAmount()));
            variables.put("endDate", subscription.getEndDate().toLocalDate().toString());

            WhatsAppRequest waRequest = WhatsAppRequest.builder()
                    .tenantId(member.getTenantId())
                    .correlationId(correlationId)
                    .leadId(member.getId())
                    .phone(member.getPhoneNumber())
                    .template("welcome_member")
                    .variables(variables)
                    .build();

            log.info("Sending welcome WhatsApp template notification to member ID: {}", member.getId());
            whatsappClient.sendMessage(waRequest);
        } catch (Exception e) {
            log.error("Failed to send welcome WhatsApp message for member ID: {}. Error: {}", member.getId(), e.getMessage());
        }

        // Email notification
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("memberName", member.getFullName());
            variables.put("planName", subscription.getPlan().getName());
            variables.put("amount", String.valueOf(payment.getTotalAmount()));
            variables.put("paymentReference", payment.getTransactionReference());
            variables.put("paymentDate", payment.getPaymentDate().toLocalDate().toString());

            EmailRequest emailRequest = EmailRequest.builder()
                    .tenantId(member.getTenantId())
                    .correlationId(correlationId)
                    .to(member.getEmail())
                    .subject("Welcome to our Gym! - Payment Receipt")
                    .template("payment_receipt")
                    .variables(variables)
                    .build();

            log.info("Sending payment receipt email notification to member email: {}", member.getEmail());
            emailClient.sendEmail(emailRequest);
        } catch (Exception e) {
            log.error("Failed to send payment receipt email to member ID: {}. Error: {}", member.getId(), e.getMessage());
        }
    }

    @Transactional
    public PaymentTransactionResponseDTO resendPaymentLink(UUID memberId, UUID tenantId) {
        log.info("Request to resend payment link for member ID: {}", memberId);
        PaymentTransaction transaction = transactionRepo.findByMemberIdAndTenantId(memberId, tenantId)
                .orElseThrow(() -> new RuntimeException("Payment transaction not found for member ID: " + memberId));

        if (PaymentStatus.SUCCESS.equals(transaction.getPaymentStatus())) {
            throw new RuntimeException("Payment already successful for this member transaction");
        }

        Member member = memberRepo.findByIdAndTenantId(memberId, tenantId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        MemberSubscription subscription = subscriptionRepo.findById(transaction.getMembershipId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        syncPaymentTransaction(transaction, member, subscription);

        return PaymentTransactionResponseDTO.builder()
                .id(transaction.getId())
                .memberId(transaction.getMemberId())
                .membershipId(transaction.getMembershipId())
                .amount(transaction.getAmount())
                .paymentStatus(transaction.getPaymentStatus().name())
                .razorpayPaymentLinkId(transaction.getRazorpayPaymentLinkId())
                .paymentLink(transaction.getPaymentLink())
                .syncStatus(transaction.getSyncStatus().name())
                .retryCount(transaction.getRetryCount())
                .createdOn(transaction.getCreatedOn())
                .build();
    }

    @Transactional
    public void retryFailedTransactions() {
        log.info("Scheduler execution: retrying failed sync operations");
        List<PaymentTransaction> failedTransactions = transactionRepo.findBySyncStatusAndPaymentStatus(
                SyncStatus.FAILED, PaymentStatus.PENDING);

        // Also look for PENDING syncs that haven't been successfully called
        List<PaymentTransaction> pendingTransactions = transactionRepo.findBySyncStatusAndPaymentStatus(
                SyncStatus.PENDING, PaymentStatus.PENDING);

        failedTransactions.addAll(pendingTransactions);

        for (PaymentTransaction transaction : failedTransactions) {
            if (transaction.getRetryCount() >= 5) {
                log.warn("Transaction ID: {} reached maximum retry attempts. Stopping automated retries.", transaction.getId());
                continue;
            }

            try {
                Member member = memberRepo.findById(transaction.getMemberId()).orElse(null);
                MemberSubscription subscription = subscriptionRepo.findById(transaction.getMembershipId()).orElse(null);

                if (member != null && subscription != null) {
                    syncPaymentTransaction(transaction, member, subscription);
                } else {
                    log.error("Unable to retry transaction ID: {}. Member or subscription not found.", transaction.getId());
                }
            } catch (Exception e) {
                log.error("Failed executing automated retry for transaction ID: {}. Error: {}", transaction.getId(), e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public PaymentTransactionResponseDTO getTransactionByMemberId(UUID memberId, UUID tenantId) {
        PaymentTransaction transaction = transactionRepo.findByMemberIdAndTenantId(memberId, tenantId)
                .orElseThrow(() -> new RuntimeException("No onboarding transaction found for member ID: " + memberId));

        return PaymentTransactionResponseDTO.builder()
                .id(transaction.getId())
                .memberId(transaction.getMemberId())
                .membershipId(transaction.getMembershipId())
                .amount(transaction.getAmount())
                .paymentStatus(transaction.getPaymentStatus().name())
                .razorpayPaymentLinkId(transaction.getRazorpayPaymentLinkId())
                .paymentLink(transaction.getPaymentLink())
                .syncStatus(transaction.getSyncStatus().name())
                .retryCount(transaction.getRetryCount())
                .createdOn(transaction.getCreatedOn())
                .build();
    }
}
