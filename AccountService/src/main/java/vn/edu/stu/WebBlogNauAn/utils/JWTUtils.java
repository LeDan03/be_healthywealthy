package vn.edu.stu.WebBlogNauAn.utils;

import io.jsonwebtoken.*;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.slf4j.Logger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Component
public class JWTUtils {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(JWTUtils.class);

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    private SecretKey getSecretKey() {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    @Value("${security.jwt.mail-expiration}")
    private long expirationMs;

    public JWTUtils() {
    }

    // Access token
    public String generateAccessToken(String email, long account_id, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("account_id", account_id)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSecretKey())
                .compact();
    }

    // Refresh token
    public String generateRefreshToken(String email, long account_id, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("account_id", account_id)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 24 * 3))// 3 ngay
                .signWith(getSecretKey())
                .compact();
    }

    // Xac thuc, lay thong tin tu token
    public Claims extractClaims(String token) {
        Claims claims = Jwts.parserBuilder()
                // .setSigningKey(secretKey)
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    // Lay email tu access token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public long extractAccountId(String token) {
        return extractClaims(token).get("account_id", Long.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // Kiem tra token con song khong
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Xac thuc token
    public boolean validateToken(String token, String email) {
        return (email.equals(extractEmail(token))) && !isTokenExpired(token); // So sanh email va email trong token,
                                                                              // token chua het han
    }

    // Register
    public String generateEmailVerificationToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSecretKey())
                .compact();
    }

}
