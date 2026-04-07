package com.gym.Elite.Gym.attendanceEvent.repo;

import com.gym.Elite.Gym.attendanceEvent.entity.Attendance;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, UUID> {

    Optional<Attendance> findByTenantAndMember_IdAndDate(
            Tenants tenant,
            UUID memberId,
            LocalDate date
    );
    @Query("""
    SELECT a FROM Attendance a
    WHERE a.tenant = :tenant
    AND a.date = :date
    AND a.checkInTime IS NOT NULL
    ORDER BY a.checkInTime DESC
""")
    List<Attendance> findTodayActiveMembers(Tenants tenant, LocalDate date);

    @Query("""
    SELECT a.date as date, COUNT(a.id) as count
    FROM Attendance a
    WHERE a.tenant = :tenant
    AND a.member.id = :memberId
    AND a.date BETWEEN :startDate AND :endDate
    GROUP BY a.date
""")
    List<Object[]> getAttendanceHeatmap(
            Tenants tenant,
            String memberId,
            LocalDate startDate,
            LocalDate endDate
    );
}
