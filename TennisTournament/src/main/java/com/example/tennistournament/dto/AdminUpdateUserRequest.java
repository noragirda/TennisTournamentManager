package com.example.tennistournament.dto;

import com.example.tennistournament.model.enums.Role;
import lombok.Data;

@Data
public class AdminUpdateUserRequest
{
    private String name;
    private String username;
    private String email;
    private Role role;
}
