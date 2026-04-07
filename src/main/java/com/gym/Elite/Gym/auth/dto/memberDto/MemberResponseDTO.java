package com.gym.Elite.Gym.auth.dto.memberDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDTO {

    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Boolean enabled;
    private Boolean active;
    private Date createdOn;
}
