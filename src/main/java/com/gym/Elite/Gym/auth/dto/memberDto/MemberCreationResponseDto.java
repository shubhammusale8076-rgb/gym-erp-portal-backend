package com.gym.Elite.Gym.auth.dto.memberDto;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCreationResponseDto {

    private UUID memberId;
    private String fullName;
    private String userName;
    private String role;
    private String temporaryPassword;
    private String paymentLink;
    private String razorpayPaymentLinkId;
}
