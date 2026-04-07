package com.gym.Elite.Gym.payment.repo;

import com.gym.Elite.Gym.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, UUID> {
    List<Payment> findByMemberId(UUID memberId);
}
