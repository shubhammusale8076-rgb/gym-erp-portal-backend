package com.gym.Elite.Gym.payment.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.auth.service.SubscriptionService;
import com.gym.Elite.Gym.payment.dto.PaymentRequestDTO;
import com.gym.Elite.Gym.payment.dto.PaymentResponseDTO;
import com.gym.Elite.Gym.payment.entity.Payment;
import com.gym.Elite.Gym.payment.entity.PaymentItem;
import com.gym.Elite.Gym.payment.mapper.PaymentMapper;
import com.gym.Elite.Gym.payment.repo.PaymentRepo;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.tenants.repo.TenantRepo;
import lombok.RequiredArgsConstructor;
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
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final TenantRepo tenantRepo;
    private final MemberRepo memberRepo;
    private final PaymentMapper paymentMapper;
    private final SubscriptionService subscriptionService;

    public ResponseDto createPayment(PaymentRequestDTO request) {

        Member member = memberRepo.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Tenants tenant = tenantRepo.findById(request.getTenantId())
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
                .status("PAID")
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

        // 🔥 CORE: Trigger subscription logic
        handleSubscriptionAfterPayment(savedPayment);

        return ResponseDto.builder().code(201).message("Payment Record Created Successfully").build();
    }

    public PaymentResponseDTO getPaymentById(UUID paymentId) {
        return paymentMapper.mapToPaymentDTO(getEntity(paymentId));
    }

    public List<PaymentResponseDTO> getPaymentsByMember(UUID memberId) {
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

        // 🔁 CASE 1: Renewal
        if (payment.getSubscriptionId() != null) {

            ResponseDto responseDto = subscriptionService.renewSubscriptionWithPayment(
                    payment.getSubscriptionId(),
                    payment
            );

        } else {
            // 🆕 CASE 2: New Subscription

            ResponseDto responseDto =  subscriptionService.createSubscriptionFromPayment(
                    payment.getMember().getId(),
                    payment.getPlanId(),
                    payment
            );
        }
    }
}
