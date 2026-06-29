package com.example.acabou_mony_auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {
    private final String SECRET_KEY = "super-secret-key";

    public String generateToken(String email) {
        long now = new Date().getTime();
        long exp = now + 86400000;
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setExpiration(new Date(exp))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token, String email) {
        try {
            String extracted = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return extracted.equals(email);
        } catch (Exception e) {
            return false;
        }
    }
}