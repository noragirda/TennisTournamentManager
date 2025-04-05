package com.example.tennistournament.service.impl;

import com.example.tennistournament.dto.AdminUpdateUserRequest;
import com.example.tennistournament.dto.UserResponse;
import com.example.tennistournament.dto.UserUpdateRequest;
import com.example.tennistournament.model.Match;
import com.example.tennistournament.model.User;
import com.example.tennistournament.repository.MatchRepository;
import com.example.tennistournament.repository.UserRepository;
import com.example.tennistournament.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return toDto(user);
    }

    @Override
    public void updateUser(Long userId, AdminUpdateUserRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public byte[] exportMatchListAsCsv() {
        List<Match> matches = matchRepository.findAll();
        StringBuilder builder = new StringBuilder();
        builder.append("Match ID,Player 1,Player 2,Referee,Date,Location,Status\n");

        for (Match match : matches) {
            builder.append(match.getId()).append(",")
                    .append(match.getPlayer1().getName()).append(",")
                    .append(match.getPlayer2().getName()).append(",")
                    .append(match.getReferee().getName()).append(",")
                    .append(match.getMatchDatetime()).append(",")
                    .append(match.getCourtName()).append(",")
                    .append(match.getStatus()).append("\n");
        }

        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] exportMatchListAsTxt() {
        List<Match> matches = matchRepository.findAll();
        StringBuilder builder = new StringBuilder();
        for (Match match : matches) {
            builder.append("Match ID: ").append(match.getId()).append("\n")
                    .append("Players: ").append(match.getPlayer1().getName())
                    .append(" vs ").append(match.getPlayer2().getName()).append("\n")
                    .append("Referee: ").append(match.getReferee().getName()).append("\n")
                    .append("Date: ").append(match.getMatchDatetime()).append("\n")
                    .append("Location: ").append(match.getCourtName()).append("\n")
                    .append("Status: ").append(match.getStatus()).append("\n\n");
        }

        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private UserResponse toDto(User user) {

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getEmail(),
                user.getRole().toString()
        );
    }

}
