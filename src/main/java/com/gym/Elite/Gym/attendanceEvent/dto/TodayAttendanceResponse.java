package com.gym.Elite.Gym.attendanceEvent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayAttendanceResponse {
    private List<AttendanceResponse> records;
    private long totalRecords;
    private long uniqueMembersCount;
    private long activeSessionsCount;
}
