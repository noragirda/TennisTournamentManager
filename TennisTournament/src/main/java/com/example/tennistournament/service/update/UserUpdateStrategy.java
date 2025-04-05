package com.example.tennistournament.service.update;

import com.example.tennistournament.dto.UserUpdateRequest;
import com.example.tennistournament.model.User;

public interface UserUpdateStrategy {
    User update(User user, UserUpdateRequest request);
}
