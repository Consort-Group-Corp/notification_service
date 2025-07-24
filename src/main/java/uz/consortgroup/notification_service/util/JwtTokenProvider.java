package uz.consortgroup.notification_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${security.token}")
    private String jetSecret;

    @Value("${security.expiration}")
    private int jetExpirationMs;

    private Key secretKey;

    @PostConstruct
    public void init() {
        try {
            secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jetSecret));
            log.info("JWT secret key initialized");
        } catch (Exception e) {
            log.error("Failed to initialize JWT secret key", e);
        }
    }

    public boolean validateToken(String token) {
        log.debug("Validating JWT token");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return !isTokenExpired(claims);
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(Claims claims) {
        Date expirationDate = claims.getExpiration();
        boolean expired = expirationDate == null || expirationDate.before(new Date());
        log.debug("Token expired: {}", expired);
        return expired;
    }

    public String getUserNameFromJwtToken(String token) {
        log.debug("Extracting username from JWT token");
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        log.debug("Extracting expiration date from JWT token");
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    public String generateToken(String username) {
        log.debug("Generating token for user: {}", username);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jetExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }
}
