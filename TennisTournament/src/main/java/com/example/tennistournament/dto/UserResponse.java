package com.example.tennistournament.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
public record UserResponse(Long id,
                           String name,
                           String email,
                           String role) {}
