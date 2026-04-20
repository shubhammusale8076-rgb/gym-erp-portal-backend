package com.gym.Elite.Gym.integration.repo;

import com.gym.Elite.Gym.integration.entity.EventOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventOutboxRepo extends JpaRepository<EventOutbox, String> {
    List<EventOutbox> findByStatus(String status);
}
