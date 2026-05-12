package com.gym.Elite.Gym.common.scheduler;

import com.gym.Elite.Gym.tenants.entity.TenantRef;
import com.gym.Elite.Gym.tenants.repo.TenantRefRepository;
import com.gym.Elite.Gym.trainer.entity.TrainerAttendance;
import com.gym.Elite.Gym.trainer.repo.TrainerAttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttendanceScheduler {

    private final TenantRefRepository tenantRepo;
    private final TrainerAttendanceRepository trainerAttendanceRepo;

    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void autoCloseTrainerAttendance() {
        log.info("Starting auto-close trainer attendance job");
        List<TenantRef> tenants = tenantRepo.findAll();

        for (TenantRef tenant : tenants) {
            List<TrainerAttendance> openAttendances = trainerAttendanceRepo
                    .findAllByTenantIdAndCheckInTimeIsNotNullAndCheckOutTimeIsNull(tenant.getTenantId());

            for (TrainerAttendance attendance : openAttendances) {
                attendance.setCheckOutTime(LocalDateTime.of(attendance.getDate(), LocalTime.MAX));
                trainerAttendanceRepo.save(attendance);
                log.info("Auto-closed attendance for trainer {} in tenant {}", attendance.getTrainerId(), tenant.getTenantId());
            }
        }
        log.info("Finished auto-close trainer attendance job");
    }
}
