package com.gym.Elite.Gym.auth.helper;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JWTTokenHelper {

    @Value("${app.jwt.secret}")
    private String secretKey;

    private SecretKey signingKey;

    private Instant serverStartTime;

    private static final String ISSUER = "elite-gym-api";

    @PostConstruct
    public void init() {
        byte[] keysBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keysBytes);
        this.serverStartTime = Instant.now();
    }

    public String generateToken(String username, UUID tenantId, Integer tokenVersion) {
        return generateToken(username, tenantId, tokenVersion, null, null, null);
    }

    public String generateToken(
            String username,
            UUID tenantId,
            Integer tokenVersion,
            UUID userId,
            String role,
            List<String> permissions) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", tenantId.toString());
        claims.put("tokenVersion", tokenVersion);
        claims.put("srv_iat", serverStartTime.toEpochMilli());

        if (userId != null) {
            claims.put("userId", userId.toString());
        }
        if (role != null) {
            claims.put("role", role);
        }
        if (permissions != null && !permissions.isEmpty()) {
            claims.put("permissions", permissions);
        }

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuer(ISSUER)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(4, ChronoUnit.HOURS)))
                .signWith(signingKey)
                .compact();
    }

    public String generateTokenFromAuthorities(
            String username,
            UUID tenantId,
            Integer tokenVersion,
            UUID userId,
            String role,
            Collection<? extends GrantedAuthority> authorities) {

        List<String> permissions = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> !auth.startsWith("ROLE_"))
                .distinct()
                .collect(Collectors.toList());

        return generateToken(username, tenantId, tokenVersion, userId, role, permissions);
    }

    public String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token, UserDetails userDetails, Integer currentTokenVersion) {
        Claims claims = getAllClaims(token);
        if (claims == null) return false;

        String username = claims.getSubject();
        String issuer = claims.getIssuer();

        if (username == null || !username.equals(userDetails.getUsername())) return false;
        if (!ISSUER.equals(issuer)) return false;
        if (isExpired(claims)) return false;

        Integer tokenVersion = claims.get("tokenVersion", Integer.class);
        if (!Objects.equals(tokenVersion, currentTokenVersion)) return false;

        Long srvIat = claims.get("srv_iat", Long.class);
        if (srvIat == null || srvIat < serverStartTime.toEpochMilli()) return false;

        return true;
    }

    private Claims getAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private boolean isExpired(Claims claims) {
        Date exp = claims.getExpiration();
        return exp == null || exp.before(new Date());
    }

    public String getUsername(String token) {
        Claims claims = getAllClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    public UUID getTenantId(String token) {
        Claims claims = getAllClaims(token);
        if (claims == null) return null;
        return UUID.fromString(claims.get("tenantId", String.class));
    }

    public UUID getUserId(String token) {
        Claims claims = getAllClaims(token);
        if (claims == null) return null;
        String userId = claims.get("userId", String.class);
        return userId != null ? UUID.fromString(userId) : null;
    }

    public String getRole(String token) {
        Claims claims = getAllClaims(token);
        return claims != null ? claims.get("role", String.class) : null;
    }

    @SuppressWarnings("unchecked")
    public List<String> getPermissions(String token) {
        Claims claims = getAllClaims(token);
        if (claims == null) return List.of();
        Object raw = claims.get("permissions");
        if (raw instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }
}
