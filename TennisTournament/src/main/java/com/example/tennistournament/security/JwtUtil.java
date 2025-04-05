package com.example.tennistournament.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // secret key

    // Method to generate JWT token
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)                    // Subject (user email)
                .claim("role", role)                  // Store role as a claim
                .setIssuedAt(new Date())              // Issue date
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiry date
                .signWith(key)                        // Sign with the secret key
                .compact();                           // Generate token
    }

    // Method to validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); // Parse and validate
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // Token is invalid or expired
        }
    }

    // Method to extract email from the token
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)  // Use the same secret key for validation
                .build()
                .parseClaimsJws(token) // Parse token
                .getBody()              // Get the body (payload)
                .getSubject();          // Extract email from subject
    }

    // Method to extract role from the token
    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)  // Use the same secret key for validation
                .build()
                .parseClaimsJws(token) // Parse token
                .getBody()              // Get the body (payload)
                .get("role", String.class);  // Extract role from "role" claim
    }
}
