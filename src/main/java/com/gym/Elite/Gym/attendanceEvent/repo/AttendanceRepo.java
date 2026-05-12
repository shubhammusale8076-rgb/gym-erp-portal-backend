package com.gym.Elite.Gym.attendanceEvent.repo;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.entity.Attendance;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, UUID> {

    // ── Session lookup ───────────────────────────────────────────────────────

    /**
     * Find the most recent open check-in session for an actor within a tenant.
     * Used to detect duplicate check-ins and to perform check-out.
     */
    Optional<Attendance> findFirstByActorIdAndActorTypeAndStatusAndTenantIdOrderByCheckInTimeDesc(
            UUID actorId,
            AttendanceActorType actorType,
            AttendanceStatus status,
            UUID tenantId
    );



    // ── Duplicate detection ──────────────────────────────────────────────────

    /**
     * Check if a check-in already exists within a configurable time window.
     */
    @Query("""
        SELECT COUNT(a) > 0 FROM Attendance a
        WHERE a.actorId = :actorId
          AND a.actorType = :actorType
          AND a.tenantId = :tenantId
          AND a.checkInTime > :since
    """)
    boolean existsRecentCheckIn(
            @Param("actorId") UUID actorId,
            @Param("actorType") AttendanceActorType actorType,
            @Param("tenantId") UUID tenantId,
            @Param("since") LocalDateTime since
    );




    @Query("""
        SELECT a FROM Attendance a
        WHERE a.tenantId = :tenantId
          AND a.status = com.gym.Elite.Gym.attendanceEvent.enums.AttendanceStatus.CHECKED_IN
          AND a.checkInTime < :before
    """)
    List<Attendance> findOpenSessionsOlderThan(
            @Param("tenantId") UUID tenantId,
            @Param("before") LocalDateTime before
    );



    Integer countByActorIdAndActorType(UUID actorId, AttendanceActorType actorType);

    List<Attendance> findTop5ByActorIdAndActorTypeOrderByCheckInTimeDesc(
            UUID actorId,
            AttendanceActorType actorType
    );
}
