package com.gym.Elite.Gym.crm.config;

import com.gym.Elite.Gym.crm.util.CorrelationIdFilter;
import com.gym.Elite.Gym.utility.SecurityUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Propagate Correlation ID
            String correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_LOG_VAR);
            if (correlationId != null) {
                requestTemplate.header(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId);
            }

            // Propagate Tenant ID
            try {
                UUID tenantId = SecurityUtils.getCurrentTenantId();
                requestTemplate.header("X-Tenant-ID", String.valueOf(tenantId));
            } catch (Exception e) {
                // Background tasks might not have a security context
            }
        };
    }
}
