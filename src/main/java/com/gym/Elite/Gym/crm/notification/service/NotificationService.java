package com.gym.Elite.Gym.crm.notification.service;

import com.gym.Elite.Gym.crm.notification.entity.Notification;
import com.gym.Elite.Gym.crm.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Async("notificationExecutor")
    @Transactional
    public void createNotification(UUID userId, String title, String message, Notification.NotificationType type, UUID tenantId) {
        log.info("Creating notification for user: {} - {}", userId, title);
        
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .tenantId(tenantId)
                .build();
        
        notificationRepository.save(notification);
    }
}
