package com.gym.Elite.Gym.auth.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class JWTTokenHelper {

    private String secretKey = " ";

    public JWTTokenHelper() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey key = keyGenerator.generateKey();
            secretKey= Base64.getEncoder().encodeToString(key.getEncoded());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String userName, UUID tenantId){

        Map<String , Object> claims = new HashMap<>();
        claims.put("tenantId", tenantId.toString());

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 4 * 60 * 60 * 1000L)) // 4 hours
                .and()
                .signWith(getSigningKey())
                .compact();

    }

    private Key getSigningKey() {
        byte[] keysBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keysBytes);
    }

    public String getToken( HttpServletRequest request ) {

        String authHeader = getAuthHeaderFromHeader( request );
        if ( authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return authHeader;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUserNameFromToken(token);
        return (
                username != null &&
                        username.equals(userDetails.getUsername()) &&
                        !isTokenExpired(token)
        );
    }

    private boolean isTokenExpired(String token) {
        Date expireDate=getExpirationDate(token);
        return expireDate.before(new Date());
    }

    public Date getExpirationDate(String token) {
        Date expireDate;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            expireDate = claims.getExpiration();
        } catch (Exception e) {
            expireDate = null;
        }
        return expireDate;
    }

    private String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public String getUserNameFromToken(String authToken) {
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(authToken);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public String getClaimFromToken(String token, String claimName) {
        Claims claims = getAllClaimsFromToken(token);
        return claims != null ? claims.get(claimName, String.class) : null;
    }

    private Claims getAllClaimsFromToken(String token){
        Claims claims;
        try{
            claims= Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (Exception e){
            claims =null;
        }
        return claims;
    }

    public UUID getTenantIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String tenantId = (String) claims.get("tenantId");
        return UUID.fromString(tenantId);
    }

}
