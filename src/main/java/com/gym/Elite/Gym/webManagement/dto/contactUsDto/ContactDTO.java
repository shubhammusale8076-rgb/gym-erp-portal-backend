package com.gym.Elite.Gym.webManagement.dto.contactUsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactDTO {

    private String supportEmail;
    private String phoneNumber;
    private String emergencyContact;
    private String address;

    private String instagram;
    private String twitter;
    private String facebook;
    private String youtube;
}
