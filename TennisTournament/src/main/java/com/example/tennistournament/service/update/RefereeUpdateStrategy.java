package com.example.tennistournament.service.update;

import com.example.tennistournament.dto.UserUpdateRequest;
import com.example.tennistournament.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefereeUpdateStrategy implements UserUpdateStrategy {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User update(User user, UserUpdateRequest request) {
        if (request.name() != null) {
            user.setName(request.name());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        return user;
    }
}
