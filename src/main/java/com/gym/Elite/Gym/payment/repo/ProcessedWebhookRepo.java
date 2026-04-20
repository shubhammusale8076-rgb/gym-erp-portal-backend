package com.gym.Elite.Gym.payment.repo;

import com.gym.Elite.Gym.payment.entity.ProcessedWebhook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedWebhookRepo extends JpaRepository<ProcessedWebhook, String> {
}
