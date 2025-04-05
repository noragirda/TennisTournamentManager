package com.example.tennistournament.service;

import com.example.tennistournament.dto.LoginRequest;
import com.example.tennistournament.dto.UserRegisterRequest;
import com.example.tennistournament.dto.UserResponse;
import com.example.tennistournament.dto.UserUpdateRequest;
import com.example.tennistournament.model.User;

public interface UserService {
    UserResponse register(UserRegisterRequest request);
    String login(LoginRequest request);
    User updateUserInfo(User user, UserUpdateRequest request);

}
