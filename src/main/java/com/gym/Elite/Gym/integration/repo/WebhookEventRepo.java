package com.gym.Elite.Gym.integration.repo;

import com.gym.Elite.Gym.integration.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WebhookEventRepo extends JpaRepository<WebhookEvent, UUID> {

    boolean existsByExternalEventId(String externalEventId);
}
