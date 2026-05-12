package com.gym.Elite.Gym.integration.service;

import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.entity.MemberSubscription;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.integration.client.IntegrationClient;
import com.gym.Elite.Gym.integration.dto.google.GoogleSheetExportRequest;
import com.gym.Elite.Gym.integration.dto.google.GoogleSheetExportResponse;
import com.gym.Elite.Gym.trainer.entity.TrainerMemberAssignment;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GoogleService {

    private final MemberRepo memberRepo;
    private final IntegrationClient integrationClient;


    public GoogleSheetExportResponse exportMembersToGoogleSheets() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        // =====================================
        // FETCH MEMBERS
        // =====================================
        List<Member> members = memberRepo.findByTenantId(tenantId);

        // =====================================
        // BUILD ROWS
        // =====================================
        List<Map<String, Object>> rows = members.stream()
                        .map(member -> {
                            // =====================================
                            // LATEST SUBSCRIPTION
                            // =====================================
                            MemberSubscription latestSubscription = member.getSubscriptions() != null &&
                                            !member.getSubscriptions().isEmpty()
                                            ? member.getSubscriptions()
                                            .stream()
                                            .reduce((first, second) -> second)
                                            .orElse(null)
                                            : null;

                            // =====================================
                            // TRAINER
                            // =====================================
                            String trainerName = null;

                            if (member.getTrainerAssignments() != null && !member.getTrainerAssignments().isEmpty()) {

                                TrainerMemberAssignment assignment = member.getTrainerAssignments().get(0);
                                if (assignment.getTrainer() != null) {
                                    trainerName = assignment.getTrainer().getFullName();
                                }
                            }

                            // =====================================
                            // ROW
                            // =====================================
                            Map<String, Object> row = new LinkedHashMap<>();

                            row.put("Member Name", member.getFullName());
                            row.put("Email", member.getEmail());
                            row.put("Phone Number", member.getPhoneNumber());
                            row.put("Address", member.getAddress());
                            row.put("Member Status", Boolean.TRUE.equals(member.getActive()) ? "ACTIVE" : "INACTIVE");
                            row.put("Subscription Plan", latestSubscription != null && latestSubscription.getPlan() != null ? latestSubscription.getPlan().getName() : "-");
                            row.put("Subscription Status", latestSubscription != null ? latestSubscription.getStatus() : "-");
                            row.put("Trainer Assigned", trainerName != null ? trainerName : "-");
                            row.put("Emergency Contact", member.getEmergencyContactName());
                            row.put("Emergency Phone", member.getEmergencyContactNumber());
                            row.put("Joined On", member.getCreatedOn());

                            return row;
                        })
                        .toList();

        // =====================================
        // REQUEST
        // =====================================
        GoogleSheetExportRequest request = GoogleSheetExportRequest.builder()
                        .tenantId(tenantId)
                        .sheetTitle("Gym Members Export")
                        .rows(rows)
                        .build();

        // =====================================
        // CALL INTEGRATION SERVICE
        // =====================================
        return integrationClient.exportMembersToGoogleSheets(request);
    }
}
