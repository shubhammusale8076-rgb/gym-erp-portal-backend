package com.gym.Elite.Gym.crm.outbox.repository;

import com.gym.Elite.Gym.crm.outbox.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    
    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(OutboxEvent.OutboxStatus status);
    
    @Query("SELECT e FROM OutboxEvent e WHERE e.status = 'FAILED' AND e.retryCount < 5")
    List<OutboxEvent> findFailedEventsToRetry();
}
