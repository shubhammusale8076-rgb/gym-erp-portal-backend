package com.gym.Elite.Gym.attendanceEvent.entity;

import com.gym.Elite.Gym.auth.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "session_bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionBooking {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Boolean attended; // optional (mark from attendance)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}