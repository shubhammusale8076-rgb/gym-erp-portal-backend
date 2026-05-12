package com.gym.Elite.Gym.integration.dto.google;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleSheetExportResponse {

    private String sheetId;
    private String sheetUrl;
    private String message;
}