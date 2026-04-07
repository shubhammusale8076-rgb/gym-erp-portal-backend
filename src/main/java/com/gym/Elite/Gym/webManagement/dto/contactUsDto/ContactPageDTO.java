package com.gym.Elite.Gym.webManagement.dto.contactUsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactPageDTO {

    private ContactDTO contact;
    private List<OperatingHoursDto> operatingHours;
}
