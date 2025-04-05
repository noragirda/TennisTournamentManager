package com.example.tennistournament.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(@NotBlank String name, @Email String email, @Size(min=6) String password, @NotBlank String role) {}

