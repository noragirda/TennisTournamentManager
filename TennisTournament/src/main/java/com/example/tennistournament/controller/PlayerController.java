package com.example.tennistournament.controller;

import com.example.tennistournament.dto.*;
import com.example.tennistournament.model.*;
import com.example.tennistournament.repository.RegistrationRepository;
import com.example.tennistournament.repository.TournametRepository;
import com.example.tennistournament.service.PlayerTournamentService;
import com.example.tennistournament.repository.UserRepository;
import com.example.tennistournament.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.tennistournament.model.enums.RegistrationStatus;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;


@RestController
@RequestMapping("/api/player")
@PreAuthorize("hasRole('PLAYER')")
public class PlayerController {

    @Autowired
    private PlayerTournamentService playerTournamentService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TournametRepository tournamentRepository;
    @Autowired
    private RegistrationRepository registrationRepository;

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
        String email = principal.getName(); // This is the email from JWT
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email '" + email + "' not found"))
                .getId();
    }

    @GetMapping("/matches")
    public ResponseEntity<List<Match>> getPlayerMatches(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(playerTournamentService.getPlayerMatches(user));
    }
    @Transactional(readOnly = true)
    @GetMapping("/available-tournaments")
    public ResponseEntity<List<TournamentResponse>> getAvailableTournaments(Principal principal) {
        String email = principal.getName();
        User player = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long playerId = player.getId();

        List<Tournament> allTournaments = tournamentRepository.findAll();

        List<Registration> playerRegistrations = registrationRepository.findApprovedRegistrationsByPlayerId(playerId);

        // Extract tournament IDs where this player is already registered
        Set<Long> registeredTournamentIds = playerRegistrations.stream()
                .map(reg -> reg.getTournament().getId())
                .collect(Collectors.toSet());

        List<TournamentResponse> response = allTournaments.stream()
                .map(t -> TournamentResponse.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .location(t.getLocation())
                        .startDate(t.getStartDate())
                        .endDate(t.getEndDate())
                        .registrationDeadline(t.getRegistrationDeadline())
                        .registered(registeredTournamentIds.contains(t.getId()))
                        .build())
                .toList();
        response.forEach(r -> System.out.println("Tournament " + r.getName() + ", Registered: " + r.isRegistered()));

        return ResponseEntity.ok(response);
    }
    @GetMapping("/matches/{matchId}/score")
    public ResponseEntity<ScoreResponse> getScoreDetails(@PathVariable Long matchId, Principal principal) {
        Match match = playerTournamentService.getMatchById(matchId);

        if (match == null || match.getScore() == null) {
            return ResponseEntity.notFound().build();
        }

        Score score = match.getScore();

        ScoreResponse response = ScoreResponse.builder()
                .player1Points(score.getPlayer1_points())
                .player2Points(score.getPlayer2_points())
                .setsWonPlayer1(score.getSets_won_player1())
                .setsWonPlayer2(score.getSets_won_player2())
                .set1(score.getSet1Score())
                .set2(score.getSet2Score())
                .set3(score.getSet3Score())
                .tiebreakPlayed(score.isTiebreak_played())
                .retirement(score.isRetirement())
                .walkover(score.isWalkover())
                .build();

        return ResponseEntity.ok(response);
    }







}
