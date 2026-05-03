package com.sessionguard.security;

import com.sessionguard.exception.TokenExpiredException;
import com.sessionguard.exception.TokenInvalidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private  String secretKey;

    @Value("${jwt.access-token-expiry-ms}")
    private  long  accessExpiry;

    @Value("${jwt.refresh-token-expiry-ms}")
    private  long refreshExpiry;

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateAccessToken(String email,Long sessionId){
        return Jwts.builder()
                .subject(email)
                .claim("sessionId",sessionId)
                .claim("type","ACCESS")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+accessExpiry))
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(String email, Long sessionId) {
        return Jwts.builder()
                .subject(email)
                .claim("sessionId", sessionId)
                .claim("type", "REFRESH")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiry))
                .signWith(getKey())
                .compact();
    }

    public Claims extractAll(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token){
        return extractAll(token).getSubject();
    }

    public Long extractSessionId(String token){
        return extractAll(token).get("sessionId",Long.class);
    }

    public boolean validateToken(String token) {
        try {
            extractAll(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Token expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenInvalidException("Invalid token");
        }
    }



}
