
package com.example.socio_meet.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String jwtSecretBase64;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private Key signingKey;

    @PostConstruct
    public void init(){
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate JWT with subject=userId and a roles claim.
     */
    public String generateToken(Long userId, Set<String> roles){
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(jwtExpirationMs)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate token signature, format and expiration.
     * Returns true only if token is valid.
     */
    public boolean validateToken(String token){
        try {
            parseClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException ex){
            return false;
        } catch (ExpiredJwtException ex){
            return false;
        } catch (UnsupportedJwtException ex){
            return false;
        } catch (IllegalArgumentException ex){
            return false;
        }
    }

    /**
     * Extract user id (subject) from token. Returns null if parsing fails.
     */
    public Long getUserIdFromToken(String token){
        try {
            Claims claims = parseClaims(token).getBody();
            String subject = claims.getSubject();
            if (subject == null) return null;
            return Long.parseLong(subject);
        } catch (Exception ex){
            return null;
        }
    }

    /**
     * Extract roles claim as a Set<String>. Returns empty set if absent or parsing fails.
     * Note: roles are typically stored as a collection (List) when deserialized from JWT.
     */
    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token){
        try {
            Claims claims = parseClaims(token).getBody();
            Object rolesObj = claims.get("roles");
            if (rolesObj == null) return Set.of();

            // JJWT will usually deserialize a JSON array into a List
            if (rolesObj instanceof List<?>){
                Set<String> roles = new HashSet<>();
                for (Object o : (List<?>) rolesObj){
                    if (o != null)
                        roles.add(o.toString());
                }
                return roles;
            }

            // If someone stored as a single string
            return Set.of(rolesObj.toString());
        } catch (Exception ex){
            return Set.of();
        }
    }

    /**
     * Check if token is expired. If token invalid, returns true (treat invalid as not usable).
     */
    public boolean isTokenExpired(String token) {
        Date exp = getExpirationDate(token);
        if (exp == null) return true;
        return exp.before(new Date());
    }

    /**
     * Get token expiration date. Returns null if token invalid.
     */
    public Date getExpirationDate(String token) {
        try {
            Claims claims = parseClaims(token).getBody();
            return claims.getExpiration();
        } catch (Exception ex) {
            return null;
        }
    }


    /**
     * Parse token and return Jws<Claims>. Internal helper that centralizes parser configuration.
     */
    private Jws<Claims> parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
    }
}
