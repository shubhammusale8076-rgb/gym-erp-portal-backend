package com.gym.Elite.Gym.integration.dto.google;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class GoogleSheetExportRequest {

    private UUID tenantId;
    private String sheetTitle;
    private List<Map<String, Object>> rows;
}