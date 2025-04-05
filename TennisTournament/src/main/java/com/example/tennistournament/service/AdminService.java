package com.example.tennistournament.service;

import com.example.tennistournament.dto.AdminUpdateUserRequest;
import com.example.tennistournament.dto.UserResponse;

import java.util.List;

public interface AdminService
{
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long userId);
    void updateUser(Long userId, AdminUpdateUserRequest request);
    void deleteUser(Long userId);
    byte[] exportMatchListAsCsv();
    byte[] exportMatchListAsTxt();
}
