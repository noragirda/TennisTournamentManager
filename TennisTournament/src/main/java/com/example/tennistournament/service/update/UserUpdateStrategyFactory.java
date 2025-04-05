package com.example.tennistournament.service.update;

import com.example.tennistournament.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUpdateStrategyFactory {

    private final PlayerUpdateStrategy playerUpdateStrategy;
    private final RefereeUpdateStrategy refereeUpdateStrategy;
    private final AdminUpdateStrategy adminUpdateStrategy;

    public UserUpdateStrategy getStrategy(Role role) {
        return switch (role) {
            case PLAYER -> playerUpdateStrategy;
            case REFEREE -> refereeUpdateStrategy;
            case ADMIN -> adminUpdateStrategy;
        };
    }
}
