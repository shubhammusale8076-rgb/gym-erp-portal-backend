package com.gym.Elite.Gym.attendanceEvent.service;

import com.gym.Elite.Gym.attendanceEvent.dto.CreateSessionDTO;
import com.gym.Elite.Gym.attendanceEvent.dto.SessionBookingDTO;
import com.gym.Elite.Gym.attendanceEvent.entity.Session;
import com.gym.Elite.Gym.attendanceEvent.entity.SessionBooking;
import com.gym.Elite.Gym.attendanceEvent.entity.SessionStatus;
import com.gym.Elite.Gym.attendanceEvent.repo.SessionBookingRepo;
import com.gym.Elite.Gym.attendanceEvent.repo.SessionRepo;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.tenants.repo.TenantRefRepository;
import com.gym.Elite.Gym.integration.client.EventPublisher;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final SessionRepo sessionRepo;
    private final SessionBookingRepo bookingRepo;
    private final MemberRepo memberRepo;
    private final TenantRefRepository tenantRefRepository;
    private final EventPublisher eventPublisher;

    public ResponseDto createSession(CreateSessionDTO dto) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();
        
        tenantRefRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Session session = Session.builder()
                .name(dto.getName())
                .date(dto.getSessionTime().toLocalDate())
                .startTime(dto.getSessionTime().toLocalTime())
                .capacity(dto.getCapacity())
                .status(SessionStatus.UPCOMING)
                .tenantId(tenantId)
                .createdAt(LocalDateTime.now())
                .build();

        sessionRepo.save(session);
        return ResponseDto.builder().code(201).message("Session Scheduled").build();
    }

    public ResponseDto updateSession(UUID sessionId, CreateSessionDTO dto) {
        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        UUID tenantId = SecurityUtils.getCurrentTenantId();
        if (!session.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Unauthorized");
        }

        session.setName(dto.getName());
        session.setDate(dto.getSessionTime().toLocalDate());
        session.setStartTime(dto.getSessionTime().toLocalTime());
        session.setCapacity(dto.getCapacity());
        session.setUpdatedAt(LocalDateTime.now());

        sessionRepo.save(session);
        return ResponseDto.builder().code(200).message("Session Updated").build();
    }

    public ResponseDto deleteSession(UUID sessionId) {
        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        UUID tenantId = SecurityUtils.getCurrentTenantId();
        if (!session.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Unauthorized");
        }

        bookingRepo.deleteAllBySession(session);
        sessionRepo.delete(session);
        return ResponseDto.builder().code(200).message("Session Deleted").build();
    }

    public ResponseDto bookSession(SessionBookingDTO dto) {

        Session session = sessionRepo.findById(dto.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Member member = memberRepo.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Capacity check
        long currentBookings = bookingRepo.countBySession_Id(session.getId());
        if (session.getCapacity() != null && currentBookings >= session.getCapacity()) {
            throw new RuntimeException("Session is full");
        }

        // Duplicate booking check
        if (bookingRepo.existsBySession_IdAndMember_Id(session.getId(), member.getId())) {
            throw new RuntimeException("Member already booked for this session");
        }

        SessionBooking booking = SessionBooking.builder()
                .session(session)
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();

        bookingRepo.save(booking);

        eventPublisher.publish("BOOKING_CREATED", session.getTenantId().toString(), booking);

        return ResponseDto.builder().code(201).message("Session Booked").build();
    }

    public ResponseDto cancelBooking(UUID sessionId, UUID memberId) {
        SessionBooking booking = bookingRepo.findBySession_IdAndMember_Id(sessionId, memberId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        bookingRepo.delete(booking);
        return ResponseDto.builder().code(200).message("Booking Canceled").build();
    }

    public List<Session> getTodaySchedule() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        return sessionRepo.findTodaySessions(tenantId, LocalDate.now());
    }
}
