package com.gym.Elite.Gym.attendanceEvent.service;

import com.gym.Elite.Gym.attendanceEvent.dto.ActiveMemberAttendanceDTO;
import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventDTO;
import com.gym.Elite.Gym.attendanceEvent.dto.HeatmapRequestDTO;
import com.gym.Elite.Gym.attendanceEvent.dto.HeatmapResponseDTO;
import com.gym.Elite.Gym.attendanceEvent.entity.Attendance;
import com.gym.Elite.Gym.attendanceEvent.entity.AttendanceEvent;
import com.gym.Elite.Gym.attendanceEvent.entity.AttendanceStatus;
import com.gym.Elite.Gym.attendanceEvent.repo.AttendanceEventRepo;
import com.gym.Elite.Gym.attendanceEvent.repo.AttendanceRepo;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.tenants.repo.TenantRefRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private final AttendanceEventRepo eventRepo;
    private final AttendanceRepo attendanceRepo;
    private final MemberRepo memberRepository;
    private final TenantRefRepository tenantRefRepository;

    private static final int SESSION_GAP_MINUTES = 90;

    public ResponseDto recordEvent(AttendanceEventDTO dto) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        tenantRefRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        AttendanceEvent event = AttendanceEvent.builder()
                .member(member)
                .eventTime(dto.getEventTime())
                .source(dto.getSource())
                .deviceId(dto.getDeviceId())
                .processed(false)
                .createdAt(LocalDateTime.now())
                .tenantId(tenantId)
                .build();
        eventRepo.save(event);

        processAttendance(tenantId, member, dto.getEventTime().toLocalDate());

        return ResponseDto.builder().code(201).message("Attendance event recorded").build();
    }

    public void processAttendance(UUID tenantId, Member member, LocalDate date) {

        List<AttendanceEvent> events =
                eventRepo.findByTenantIdAndMember_IdAndEventTimeBetweenOrderByEventTimeAsc(
                        tenantId,
                        member.getId(),
                        date.atStartOfDay(),
                        date.atTime(23, 59, 59)
                );

        if (events.isEmpty()) return;

        Attendance attendance = attendanceRepo
                .findByTenantIdAndMember_IdAndDate(tenantId, member.getId(), date)
                .orElseGet(() -> {
                    Attendance a = Attendance.builder()
                            .member(member)
                            .date(date)
                            .tenantId(tenantId)
                            .build();
                    return a;
                });

        // ✅ First event = check-in
        LocalDateTime checkIn = events.get(0).getEventTime();

        LocalDateTime checkOut = null;

        // ✅ Gap-based detection
        for (int i = 1; i < events.size(); i++) {

            long gap = Duration.between(
                    events.get(i - 1).getEventTime(),
                    events.get(i).getEventTime()
            ).toMinutes();

            if (gap > SESSION_GAP_MINUTES) {
                checkOut = events.get(i - 1).getEventTime();
                break;
            }
        }

        // ✅ If continuous events → last is checkout
        if (checkOut == null && events.size() > 1) {
            checkOut = events.get(events.size() - 1).getEventTime();
        }

        attendance.setCheckInTime(checkIn);
        attendance.setCheckOutTime(checkOut);

        if (checkOut != null) {
            int duration = (int) Duration.between(checkIn, checkOut).toMinutes();
            attendance.setTotalDurationMinutes(duration);
            attendance.setStatus(AttendanceStatus.PRESENT);
        } else {
            attendance.setStatus(AttendanceStatus.IN_PROGRESS);
        }

        attendance.setUpdatedAt(LocalDateTime.now());

        attendanceRepo.save(attendance);
    }

    public List<ActiveMemberAttendanceDTO> getTodayActiveMembers() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        tenantRefRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        LocalDate today = LocalDate.now();

        List<Attendance> records =
                attendanceRepo.findTodayActiveMembers(tenantId, today);

        return records.stream()
                .map(a -> ActiveMemberAttendanceDTO.builder()
                        .memberId(a.getMember().getId())
                        .memberName(a.getMember().getFullName())
                        .membershipType("Standard")
                        .checkInTime(a.getCheckInTime())
                        .profileImage("Null")
                        .build())
                .toList();
    }

    public List<HeatmapResponseDTO> getHeatmap(HeatmapRequestDTO request) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        tenantRefRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        // ✅ Range logic
        if (request.getStartDate() != null && request.getEndDate() != null) {
            startDate = request.getStartDate();
            endDate = request.getEndDate();
        } else {
            switch (request.getRange()) {
                case "7D" -> startDate = endDate.minusDays(7);
                case "30D" -> startDate = endDate.minusDays(30);
                case "6M" -> startDate = endDate.minusMonths(6);
                default -> startDate = endDate.minusDays(30);
            }
        }

        List<Object[]> rawData = attendanceRepo.getAttendanceHeatmap(
                tenantId,
                request.getMemberId(),
                startDate,
                endDate
        );

        // 🔥 Convert to map for fast lookup
        Map<LocalDate, Integer> attendanceMap = new HashMap<>();

        for (Object[] row : rawData) {
            LocalDate date = (LocalDate) row[0];
            Long count = (Long) row[1];

            attendanceMap.put(date, count.intValue());
        }

        // 🔥 Fill missing dates (VERY IMPORTANT for UI)
        List<HeatmapResponseDTO> response = new ArrayList<>();

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {

            response.add(
                    HeatmapResponseDTO.builder()
                            .date(current)
                            .count(attendanceMap.getOrDefault(current, 0))
                            .build()
            );

            current = current.plusDays(1);
        }

        return response;
    }
    // ✅ Fetch for UI

}
