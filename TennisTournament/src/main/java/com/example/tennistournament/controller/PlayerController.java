package com.example.tennistournament.controller;

import com.example.tennistournament.dto.MatchScheduleResponse;
import com.example.tennistournament.dto.MatchScoreResponse;
import com.example.tennistournament.dto.TournamentRegistrationRequest;
import com.example.tennistournament.model.Match;
import com.example.tennistournament.model.Score;
import com.example.tennistournament.model.User;
import com.example.tennistournament.service.PlayerTournamentService;
import com.example.tennistournament.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/player")
@PreAuthorize("hasRole('PLAYER')")
public class PlayerController {

    @Autowired
    private PlayerTournamentService playerTournamentService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register-tournament")
    public ResponseEntity<String> registerToTournament(@RequestBody @Valid TournamentRegistrationRequest request, Principal principal) {
        Long playerId = getPlayerId(principal);
        playerTournamentService.registerToTournament(playerId, request.getTournamentId());
        return ResponseEntity.ok("Registration successful.");
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<MatchScheduleResponse>> getSchedule(Principal principal) {
        Long playerId = getPlayerId(principal);
        List<Match> matches = playerTournamentService.getPlayerSchedule(playerId);
        List<MatchScheduleResponse> response = matches.stream().map(match -> {
            User opponent = match.getPlayer1().getId().equals(playerId)
                    ? match.getPlayer2()
                    : match.getPlayer1();
            return MatchScheduleResponse.builder()
                    .matchId(match.getId())
                    .opponent(opponent.getName())
                    .matchDateTime(match.getMatchDatetime())
                    .round(match.getRound().name())
                    .location(match.getCourtName())
                    .build();
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/scores")
    public ResponseEntity<List<MatchScoreResponse>> getScores(Principal principal) {
        Long playerId = getPlayerId(principal);
        List<Match> matches = playerTournamentService.getPlayerSchedule(playerId);
        List<MatchScoreResponse> response = matches.stream()
                .filter(match -> match.getScore() != null)
                .map(match -> {
                    User opponent = match.getPlayer1().getId().equals(playerId)
                            ? match.getPlayer2()
                            : match.getPlayer1();
                    Score score = match.getScore();
                    return MatchScoreResponse.builder()
                            .matchId(match.getId())
                            .opponent(opponent.getName())
                            .score(score.getScoreValue())
                            .result(score.getWinner_id().getId().equals(playerId) ? "Win" : "Loss")
                            .build();
                }).toList();

        return ResponseEntity.ok(response);
    }

    private Long getPlayerId(Principal principal) {
        String username = principal.getName();
        return userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
