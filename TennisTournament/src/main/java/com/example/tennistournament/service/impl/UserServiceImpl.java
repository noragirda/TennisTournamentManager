package com.example.tennistournament.service.impl;

import com.example.tennistournament.dto.LoginRequest;
import com.example.tennistournament.dto.UserRegisterRequest;
import com.example.tennistournament.dto.UserResponse;
import com.example.tennistournament.dto.UserUpdateRequest;
import com.example.tennistournament.model.User;
import com.example.tennistournament.model.enums.Role;
import com.example.tennistournament.repository.UserRepository;
import com.example.tennistournament.security.JwtUtil; // ✅ Make sure you import this
import com.example.tennistournament.service.UserService;
import com.example.tennistournament.service.update.UserUpdateStrategy;
import com.example.tennistournament.service.update.UserUpdateStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // ✅ Inject the JwtUtil
    private final UserUpdateStrategyFactory userUpdateStrategyFactory;

    @Override
    public UserResponse register(UserRegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.valueOf(request.role().toUpperCase()))
                .build();

        user = userRepository.save(user);

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    @Override
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return user; // return full user instead of token
    }

    @Override
    public User updateUserInfo(User user, UserUpdateRequest request) {
        UserUpdateStrategy strategy = userUpdateStrategyFactory.getStrategy(user.getRole());
        User updatedUser = strategy.update(user, request);
        return userRepository.save(updatedUser);
    }
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


}
