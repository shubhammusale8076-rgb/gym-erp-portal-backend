package com.gym.Elite.Gym.attendanceEvent.scheduler;

import com.gym.Elite.Gym.attendanceEvent.entity.Attendance;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceStatus;
import com.gym.Elite.Gym.attendanceEvent.repo.AttendanceRepo;
import com.gym.Elite.Gym.tenants.repo.TenantRefRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Enterprise Scheduler for automatic attendance lifecycle management.
 * Processes all actor types (Members, Trainers, Staff) to ensure no orphaned sessions.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UnifiedAttendanceScheduler {

    private final AttendanceRepo attendanceRepo;
    private final TenantRefRepository tenantRepo;
    /**
     * Automatically check-out orphaned sessions.
     * Runs every hour to clean up sessions older than a threshold (e.g., 6 hours).
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void autoCheckOutOrphanedSessions() {
        log.info("Starting Enterprise Unified Auto-Checkout Task...");
        
        List<UUID> tenantIds = tenantRepo.findAll().stream().map(t -> t.getTenantId()).toList();
        LocalDateTime threshold = LocalDateTime.now().minusHours(6);

        for (UUID tenantId : tenantIds) {
            List<Attendance> openSessions = attendanceRepo.findOpenSessionsOlderThan(tenantId, threshold);
            
            if (!openSessions.isEmpty()) {
                log.info("Found {} stale sessions for tenant {}", openSessions.size(), tenantId);
                openSessions.forEach(this::performAutoCheckOut);
            }
        }
    }

    private void performAutoCheckOut(Attendance attendance) {
        log.info("Auto-checking out {} session: {}", attendance.getActorType(), attendance.getId());
        
        // Use a default check-out time (e.g., check-in time + 1 hour or current time)
        LocalDateTime autoTime = LocalDateTime.now();
        attendance.setCheckOutTime(autoTime);
        attendance.setStatus(AttendanceStatus.COMPLETED);
        attendance.setNotes((attendance.getNotes() != null ? attendance.getNotes() : "") + 
                           " [AUTO-CHECKOUT]");

        long duration = Duration.between(attendance.getCheckInTime(), autoTime).toMinutes();
        attendance.setTotalDurationMinutes((int) duration);

        attendanceRepo.save(attendance);
    }
}
