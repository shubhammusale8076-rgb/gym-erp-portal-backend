package com.gym.Elite.Gym.payment.repo;

import com.gym.Elite.Gym.payment.entity.PaymentStatus;
import com.gym.Elite.Gym.payment.entity.PaymentTransaction;
import com.gym.Elite.Gym.payment.entity.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction, UUID> {

    Optional<PaymentTransaction> findByRazorpayPaymentLinkId(String razorpayPaymentLinkId);

    Optional<PaymentTransaction> findByMemberIdAndTenantId(UUID memberId, UUID tenantId);

    List<PaymentTransaction> findBySyncStatusAndPaymentStatus(SyncStatus syncStatus, PaymentStatus paymentStatus);
}
