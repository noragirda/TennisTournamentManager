package com.example.tennistournament.controller;

import com.example.tennistournament.dto.LoginRequest;
import com.example.tennistournament.dto.UserRegisterRequest;
import com.example.tennistournament.dto.UserResponse;
import com.example.tennistournament.model.User;
import com.example.tennistournament.security.JwtUtil;
import com.example.tennistournament.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        // Authenticate user (you probably already do this in your real method)
        User user = userService.login(request);

        // Generate token
        String token = jwtService.generateToken(user.getEmail(), user.getRole().toString());

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());

        return ResponseEntity.ok(response);
    }
}
