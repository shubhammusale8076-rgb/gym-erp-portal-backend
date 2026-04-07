package com.gym.Elite.Gym.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class SecurityUtils {

    public static UUID getCurrentTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getDetails() == null) {
            throw new RuntimeException("No authentication details found");
        }

        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();

        Object tenantIdObj = details.get("tenantId");

        if (tenantIdObj == null) {
            throw new RuntimeException("Tenant ID not found");
        }

        return (UUID) tenantIdObj;
    }
}
