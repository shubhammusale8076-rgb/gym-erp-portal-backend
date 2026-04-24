package com.gym.Elite.Gym.attendanceEvent.repo;

import com.gym.Elite.Gym.attendanceEvent.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepo extends JpaRepository<Session, UUID> {

    @Query("""
    SELECT s FROM Session s
    WHERE s.tenantId = :tenantId
    AND s.date = :today
    ORDER BY s.startTime ASC
""")
    List<Session> findTodaySessions(UUID tenantId, LocalDate today);


}
