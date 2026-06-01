package com.gym.Elite.Gym.crm.service;

import com.gym.Elite.Gym.auth.entity.GymUser;
import com.gym.Elite.Gym.crm.dto.TaskCreateRequest;
import com.gym.Elite.Gym.crm.dto.TaskResponseDto;
import com.gym.Elite.Gym.crm.entity.Lead;
import com.gym.Elite.Gym.crm.entity.LeadTask;
import com.gym.Elite.Gym.crm.enums.ActivityType;
import com.gym.Elite.Gym.crm.exception.LeadNotFoundException;
import com.gym.Elite.Gym.crm.repository.LeadRepository;
import com.gym.Elite.Gym.crm.repository.LeadTaskRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LeadTaskService {

    private final LeadTaskRepository taskRepository;
    private final LeadRepository     leadRepository;
    private final LeadServiceImpl    leadService;

    public TaskResponseDto createTask(UUID leadId, TaskCreateRequest request) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Lead lead = leadRepository.findByIdAndTenantIdAndDeletedFalse(leadId, tenantId)
                .orElseThrow(() -> new LeadNotFoundException("Lead not found: " + leadId));

        LeadTask task = LeadTask.builder()
                .tenantId(tenantId)
                .lead(lead)
                .title(request.getTitle())
                .completed(false)
                .dueDate(request.getDueDate())
                .assignedTo(request.getAssignedTo() != null ? GymUser.builder().id(request.getAssignedTo()).build() : null)
                .build();

        task = taskRepository.save(task);

        leadService.createActivity(lead, ActivityType.TASK_ADDED,
                "Task Added",
                "New task '" + request.getTitle() + "' added by " + getCurrentUsername());

        return toDto(task);
    }

    public TaskResponseDto completeTask(UUID taskId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        LeadTask task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new LeadNotFoundException("Task not found: " + taskId));

        task.setCompleted(true);
        task = taskRepository.save(task);

        leadService.createActivity(task.getLead(), ActivityType.TASK_COMPLETED,
                "Task Completed",
                "Task '" + task.getTitle() + "' marked as completed by " + getCurrentUsername());

        return toDto(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksForLead(UUID leadId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        return taskRepository.findByLeadIdAndTenantIdOrderByDueDateAsc(leadId, tenantId)
                .stream().map(this::toDto).toList();
    }

    private TaskResponseDto toDto(LeadTask t) {
        return TaskResponseDto.builder()
                .id(t.getId())
                .leadId(t.getLead().getId())
                .title(t.getTitle())
                .completed(t.getCompleted())
                .dueDate(t.getDueDate())
                .assignedTo(t.getAssignedTo() != null ? t.getAssignedTo().getId() : null)
                .createdAt(t.getCreatedAt())
                .build();
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
