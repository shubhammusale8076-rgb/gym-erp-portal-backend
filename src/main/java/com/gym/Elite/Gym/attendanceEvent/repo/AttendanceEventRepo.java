package com.gym.Elite.Gym.attendanceEvent.repo;

import com.gym.Elite.Gym.attendanceEvent.entity.AttendanceEvent;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceEventRepo extends JpaRepository<AttendanceEvent, UUID> {

    List<AttendanceEvent> findByTenantAndMember_IdAndEventTimeBetweenOrderByEventTimeAsc(
            Tenants tenant,
            UUID memberId,
            LocalDateTime start,
            LocalDateTime end
    );}
