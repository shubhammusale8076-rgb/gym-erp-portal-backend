package com.gym.Elite.Gym.attendanceEvent.repo;

import com.gym.Elite.Gym.attendanceEvent.entity.Session;
import com.gym.Elite.Gym.attendanceEvent.entity.SessionBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionBookingRepo extends JpaRepository<SessionBooking, UUID> {

    @Query("""
    SELECT COUNT(sb) FROM SessionBooking sb
    WHERE sb.session.id = :sessionId
""")
    long countBookings(UUID sessionId);

    void deleteAllBySession(Session session);

    boolean existsBySession_IdAndMember_Id(UUID sessionId, UUID memberId);

    long countBySession_Id(UUID sessionId);

    Optional<SessionBooking> findBySession_IdAndMember_Id(UUID sessionId, UUID memberId);
}
