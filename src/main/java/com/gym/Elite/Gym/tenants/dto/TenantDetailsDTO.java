package com.gym.Elite.Gym.tenants.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDetailsDTO {

    private UUID id;
    private String gymName;
    private String ownerName;
    private String email;
    private String phoneNumber;
    private String plan;
    private Boolean status;
    private Date createdOn;
}
