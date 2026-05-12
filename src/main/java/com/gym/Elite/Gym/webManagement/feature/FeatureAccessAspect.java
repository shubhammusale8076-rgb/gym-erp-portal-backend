package com.gym.Elite.Gym.webManagement.feature;

import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class FeatureAccessAspect {

    private final FeatureAccessService featureAccessService;

    @Before("@within(requireFeature) || @annotation(requireFeature)")
    public void checkFeatureAccess(RequireFeature requireFeature) {
        if (requireFeature == null) return;
        
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        featureAccessService.validateAccess(tenantId, requireFeature.value());
    }
}
