package com.gym.Elite.Gym.auth.helper;

import com.gym.Elite.Gym.auth.service.UsersDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTTokenHelper jwtTokenHelper;
    private final UsersDetailService usersDetailService;

    public JWTAuthenticationFilter(JWTTokenHelper jwtTokenHelper, UsersDetailService usersDetailService) {
        this.jwtTokenHelper = jwtTokenHelper;
        this.usersDetailService = usersDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (null == authHeader || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return;
        }

        String authToken = authHeader.substring(7);

        try {
            if(SecurityContextHolder.getContext().getAuthentication() == null){
                String userName = jwtTokenHelper.getUserNameFromToken(authToken);
                UUID tenantId = jwtTokenHelper.getTenantIdFromToken(authToken);

                if(null != userName){
                    UserDetails userDetails = usersDetailService.loadUserByUsername(userName);

                    if(jwtTokenHelper.validateToken(authToken,userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        Map<String, Object> details = new HashMap<>();
                        details.put("tenantId", tenantId);
                        details.put("requestDetails", new WebAuthenticationDetailsSource().buildDetails(request));

                        authenticationToken.setDetails(details);

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
        } finally {
            filterChain.doFilter(request, response);
        }

    }
}
