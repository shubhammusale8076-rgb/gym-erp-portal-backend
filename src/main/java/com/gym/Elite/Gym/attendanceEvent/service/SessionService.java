package com.gym.Elite.Gym.attendanceEvent.service;

import com.gym.Elite.Gym.attendanceEvent.dto.CreateSessionDTO;
import com.gym.Elite.Gym.attendanceEvent.dto.TodaySessionDTO;
import com.gym.Elite.Gym.attendanceEvent.entity.Session;
import com.gym.Elite.Gym.attendanceEvent.entity.SessionBooking;
import com.gym.Elite.Gym.attendanceEvent.entity.SessionStatus;
import com.gym.Elite.Gym.attendanceEvent.repo.SessionBookingRepo;
import com.gym.Elite.Gym.attendanceEvent.repo.SessionRepo;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.integration.client.IntegrationClient;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.tenants.repo.TenantRepo;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final SessionRepo sessionRepo;
    private final SessionBookingRepo sessionBookingRepo;
    private final TenantRepo tenantRepo;
    private final MemberRepo memberRepo;
    private final IntegrationClient integrationClient;

    public List<TodaySessionDTO> getTodaySchedule() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Tenants tenant = tenantRepo.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Session> sessions = sessionRepo.findTodaySessions(tenant, today);

        return sessions.stream().map(session -> {

            long bookings = sessionBookingRepo.countBookings(session.getId());

            int joinRate = session.getCapacity() == 0 ? 0 :
                    (int) ((bookings * 100) / session.getCapacity());

            // 🔥 Status detection
            String status;
            String tag = null;

            if (now.isBefore(session.getStartTime())) {

                long minutes = Duration.between(now, session.getStartTime()).toMinutes();
                status = "UPCOMING";
                tag = "IN " + minutes + "M";

            } else if (now.isAfter(session.getEndTime())) {

                status = "COMPLETED";

            } else {

                status = "ONGOING";
            }

            return TodaySessionDTO.builder()
                    .sessionId(session.getId())
                    .name(session.getName())
                    .time(formatTime(session.getStartTime()))
                    .location(session.getLocation())
                    .status(status)
                    .tag(tag)
                    .joinRate(joinRate)
                    .build();

        }).toList();
    }

    public ResponseDto createSession(CreateSessionDTO dto) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Tenants tenant = tenantRepo.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Session session = Session.builder()
                .tenant(tenant)
                .name(dto.getName())
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .location(dto.getLocation())
                .capacity(dto.getCapacity())
                .trainerName(dto.getTrainerName())
                .status(SessionStatus.UPCOMING)
                .createdAt(LocalDateTime.now())
                .build();

        sessionRepo.save(session);

        return ResponseDto.builder().code(201).message("Session created successfully").build();
    }

    public ResponseDto updateSession(UUID sessionId, CreateSessionDTO dto) {

        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setName(dto.getName());
        session.setDate(dto.getDate());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        session.setLocation(dto.getLocation());
        session.setCapacity(dto.getCapacity());
        session.setTrainerName(dto.getTrainerName());
        session.setUpdatedAt(LocalDateTime.now());

        sessionRepo.save(session);

        return ResponseDto.builder().code(201).message("Session updated successfully").build();
    }

    public ResponseDto deleteSession(UUID sessionId) {

        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        sessionBookingRepo.deleteAllBySession(session);

        sessionRepo.delete(session);

        return ResponseDto.builder().code(200).message("Session Deleted successfully").build();

    }

    public ResponseDto bookSession(UUID sessionId, UUID memberId) {

        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // ❗ Prevent duplicate booking
        boolean alreadyBooked = sessionBookingRepo
                .existsBySession_IdAndMember_Id(sessionId, memberId);

        if (alreadyBooked) {
            throw new RuntimeException("Already booked");
        }

        // ❗ Capacity check
        long currentCount = sessionBookingRepo.countBySession_Id(sessionId);

        if (currentCount >= session.getCapacity()) {
            throw new RuntimeException("Session full");
        }

        SessionBooking booking = SessionBooking.builder()
                .session(session)
                .member(member)
                .attended(false)
                .createdAt(LocalDateTime.now())
                .build();

        sessionBookingRepo.save(booking);

        // 🚀 Trigger event
        integrationClient.sendEvent("BOOKING_CREATED", session.getTenant().getId().toString(), booking);

        return ResponseDto.builder().code(200).message("Session Booked successfully").build();
    }

    public ResponseDto cancelBooking(UUID sessionId, UUID memberId) {

        SessionBooking booking = sessionBookingRepo
                .findBySession_IdAndMember_Id(sessionId, memberId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        sessionBookingRepo.delete(booking);

        return ResponseDto.builder().code(200).message("Booking cancelled").build();
    }

    private String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }
}
