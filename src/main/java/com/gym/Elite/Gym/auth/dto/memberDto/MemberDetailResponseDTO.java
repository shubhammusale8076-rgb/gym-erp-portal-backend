package com.gym.Elite.Gym.auth.dto.memberDto;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceDTO;
import com.gym.Elite.Gym.auth.dto.membershipPlanDto.PlanDTO;
import com.gym.Elite.Gym.payment.dto.TransactionDTO;
import com.gym.Elite.Gym.trainer.dto.TrainerMemberDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MemberDetailResponseDTO {

    private UUID memberId;
    private String memberCode; // AUR-9872

    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String status;

    private String profileImageUrl;

    // 🔥 KPIs
    private LocalDate joinDate;
    private LocalDate membershipExpiry;
    private Integer totalAttendance;
    private Double accountBalance;

    // 🔥 Plan
    private PlanDTO plan;

    private TrainerMemberDTO  trainerMemberDTO;

    // 🔥 Transactions
    private List<TransactionDTO> recentTransactions;

    // 🔥 Attendance Timeline
    private List<AttendanceDTO> attendanceTimeline;

    // 🔥 Financial
    private FinancialDTO financial;
}
