package com.example.tennistournament.controller;

import com.example.tennistournament.dto.UserResponse;
import com.example.tennistournament.dto.UserUpdateRequest;
import com.example.tennistournament.model.User;
import com.example.tennistournament.security.CustomUserDetails;
import com.example.tennistournament.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController
{
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        ));
    }
    @PutMapping("/me/update")
    public ResponseEntity<UserResponse> updateMyAccount(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest request) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User updatedUser = userService.updateUserInfo(userDetails.getUser(), request); // ✅ now it works

        return ResponseEntity.ok(new UserResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getRole().name()
        ));
    }


}
