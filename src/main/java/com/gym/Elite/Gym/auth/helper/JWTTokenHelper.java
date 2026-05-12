package com.gym.Elite.Gym.auth.helper;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class JWTTokenHelper {

    @Value("${app.jwt.secret}")
    private String secretKey;

    private SecretKey signingKey;

    // 🔥 changes on every restart
    private Instant serverStartTime;

    private static final String ISSUER = "elite-gym-api";

    @PostConstruct
    public void init() {
        byte[] keysBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keysBytes);
        this.serverStartTime = Instant.now();
    }

    // =========================
    // TOKEN GENERATION
    // =========================
    public String generateToken(String username, UUID tenantId, Integer tokenVersion) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", tenantId.toString());
        claims.put("tokenVersion", tokenVersion);
        claims.put("srv_iat", serverStartTime.toEpochMilli()); // 🔥 restart invalidation

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuer(ISSUER)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(4, ChronoUnit.HOURS)))
                .signWith(signingKey)
                .compact();
    }

    // =========================
    // TOKEN EXTRACTION
    // =========================
    public String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // =========================
    // VALIDATION (MAIN FIX)
    // =========================
    public boolean validateToken(String token, UserDetails userDetails, Integer currentTokenVersion) {

        Claims claims = getAllClaims(token);
        if (claims == null) return false;

        String username = claims.getSubject();
        String issuer = claims.getIssuer();

        // 🔥 1. Basic checks
        if (username == null || !username.equals(userDetails.getUsername())) return false;
        if (!ISSUER.equals(issuer)) return false;
        if (isExpired(claims)) return false;

        // 🔥 2. Token version check (logout / password reset)
        Integer tokenVersion = claims.get("tokenVersion", Integer.class);
        if (!Objects.equals(tokenVersion, currentTokenVersion)) return false;

        // 🔥 3. Restart invalidation check
        Long srvIat = claims.get("srv_iat", Long.class);
        if (srvIat == null || srvIat < serverStartTime.toEpochMilli()) return false;

        return true;
    }

    // =========================
    // CLAIM HELPERS
    // =========================
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
}