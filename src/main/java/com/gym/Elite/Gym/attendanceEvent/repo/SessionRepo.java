package com.gym.Elite.Gym.attendanceEvent.repo;

import com.gym.Elite.Gym.attendanceEvent.entity.Session;
import com.gym.Elite.Gym.tenants.entity.Tenants;
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
    WHERE s.tenant = :tenant
    AND s.date = :today
    ORDER BY s.startTime ASC
""")
    List<Session> findTodaySessions(Tenants tenant, LocalDate today);


}
