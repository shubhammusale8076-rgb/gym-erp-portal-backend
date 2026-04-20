package com.gym.Elite.Gym.payment.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.auth.service.SubscriptionService;
import com.gym.Elite.Gym.integration.client.EventPublisher;
import com.gym.Elite.Gym.payment.dto.PaymentRequestDTO;
import com.gym.Elite.Gym.payment.dto.PaymentResponseDTO;
import com.gym.Elite.Gym.payment.entity.Payment;
import com.gym.Elite.Gym.payment.entity.PaymentItem;
import com.gym.Elite.Gym.payment.entity.PaymentStatus;
import com.gym.Elite.Gym.payment.mapper.PaymentMapper;
import com.gym.Elite.Gym.payment.repo.PaymentRepo;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.tenants.repo.TenantRepo;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final TenantRepo tenantRepo;
    private final MemberRepo memberRepo;
    private final PaymentMapper paymentMapper;
    private final SubscriptionService subscriptionService;
    private final EventPublisher eventPublisher;

    public ResponseDto createPayment(PaymentRequestDTO request) {

        UUID currentTenantId = SecurityUtils.getCurrentTenantId();

        Member member = memberRepo.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!member.getTenant().getId().equals(currentTenantId)) {
            throw new RuntimeException("Member does not belong to your tenant");
        }

        Tenants tenant = tenantRepo.findById(currentTenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Payment payment = Payment.builder()
                .transactionReference(generateReference())
                .subtotal(request.getSubtotal())
                .taxAmount(request.getTaxAmount())
                .discountAmount(request.getDiscountAmount())
                .totalAmount(request.getTotalAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .cardLast4(request.getCardLast4())
                .status(PaymentStatus.PENDING)
                .paymentDate(new Date())
                .member(member)
                .tenant(tenant)
                .planId(request.getPlanId())
                .subscriptionId(request.getSubscriptionId())
                .build();

        List<PaymentItem> items = request.getItems().stream()
                .map(i -> PaymentItem.builder()
                        .name(i.getName())
                        .description(i.getDescription())
                        .amount(i.getAmount())
                        .payment(payment)
                        .build())
                .collect(Collectors.toList());

        payment.setItems(items);

        Payment savedPayment = paymentRepo.save(payment);

        eventPublisher.publish("PAYMENT_INITIATED", currentTenantId.toString(), savedPayment);

        return ResponseDto.builder()
                .code(201)
                .message("Payment Record Created Successfully")
                .id(savedPayment.getId())
                .build();
    }

    public void confirmPayment(UUID paymentId, String transactionReference) {
        Payment payment = getEntity(paymentId);

        if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
            log.info("Payment {} already confirmed", paymentId);
            return;
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionReference(transactionReference);
        payment.setPaymentDate(new Date());
        paymentRepo.save(payment);

        handleSubscriptionAfterPayment(payment);

        eventPublisher.publish("PAYMENT_SUCCESS", payment.getTenant().getId().toString(), payment);
    }

    public PaymentResponseDTO getPaymentById(UUID paymentId) {
        Payment payment = getEntity(paymentId);
        UUID currentTenantId = SecurityUtils.getCurrentTenantId();
        if (!payment.getTenant().getId().equals(currentTenantId)) {
            throw new RuntimeException("Unauthorized");
        }
        return paymentMapper.mapToPaymentDTO(payment);
    }

    public List<PaymentResponseDTO> getPaymentsByMember(UUID memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        UUID currentTenantId = SecurityUtils.getCurrentTenantId();
        if (!member.getTenant().getId().equals(currentTenantId)) {
            throw new RuntimeException("Unauthorized");
        }

        return paymentRepo.findByMemberId(memberId)
                .stream()
                .map(paymentMapper::mapToPaymentDTO)
                .collect(Collectors.toList());
    }

    private String generateReference() {
        return "TRX-" + new Random().nextInt(100000);
    }

    private Payment getEntity(UUID id) {
        return paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    private void handleSubscriptionAfterPayment(Payment payment) {
        if (payment.getSubscriptionId() != null) {
            subscriptionService.renewSubscriptionWithPayment(
                    payment.getSubscriptionId(),
                    payment
            );
        } else {
            subscriptionService.createSubscriptionFromPayment(
                    payment.getMember().getId(),
                    payment.getPlanId(),
                    payment
            );
        }
    }
}
