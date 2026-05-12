package com.gym.Elite.Gym.crm.notification.controller;

import com.gym.Elite.Gym.crm.notification.entity.Notification;
import com.gym.Elite.Gym.crm.notification.repository.NotificationRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/crm/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        // In a real app, we'd also filter by userId from security context
        return ResponseEntity.ok(notificationRepository.findByTenantIdOrderByCreatedAtDesc(tenantId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        notificationRepository.findById(id)
                .filter(n -> n.getTenantId().equals(tenantId))
                .ifPresent(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });
        return ResponseEntity.ok().build();
    }
}
