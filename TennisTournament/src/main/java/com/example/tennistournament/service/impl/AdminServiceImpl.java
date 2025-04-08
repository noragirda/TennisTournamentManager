package com.example.tennistournament.service.impl;

import com.example.tennistournament.dto.AdminTournamentResponse;
import com.example.tennistournament.dto.AdminUpdateUserRequest;
import com.example.tennistournament.dto.UserResponse;
import com.example.tennistournament.dto.UserUpdateRequest;
import com.example.tennistournament.model.*;
import com.example.tennistournament.model.enums.MatchStatus;
import com.example.tennistournament.model.enums.MatchRound;
import com.example.tennistournament.model.enums.RegistrationStatus;
import com.example.tennistournament.repository.MatchRepository;
import com.example.tennistournament.repository.RegistrationRepository;
import com.example.tennistournament.repository.TournametRepository;
import com.example.tennistournament.repository.UserRepository;
import com.example.tennistournament.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private TournametRepository tournamentRepository;
    @Autowired
    private RegistrationRepository registrationRepository;

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
                user.getName(),
                user.getEmail(),
                user.getRole().toString()
        );
    }
    @Override
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }
    @Override
    public void generateMatches(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        List<User> players = new ArrayList<>(
                registrationRepository
                        .findApprovedRegistrationsByTournamentId(tournamentId, RegistrationStatus.APPROVED)
                        .stream()
                        .map(Registration::getPlayer)
                        .toList()
        );
        Collections.shuffle(players);


        System.out.println("Approved players: " + players.size());

        List<User> referees = new ArrayList<>(tournament.getReferees());
        System.out.println("Assigned referees: " + referees.size());

        if (players.size() < 2 || referees.isEmpty()) {
            throw new RuntimeException("Not enough players or referees to create matches");
        }

        Collections.shuffle(players);
        Collections.shuffle(referees);

        int matchCount = players.size() / 2;

        for (int i = 0; i < matchCount; i++) {
            User player1 = players.get(i * 2);
            User player2 = players.get(i * 2 + 1);
            User referee = referees.get(i % referees.size());

            Match match = Match.builder()
                    .tournament(tournament)
                    .player1(player1)
                    .player2(player2)
                    .referee(referee)
                    .matchDatetime(LocalDateTime.now().plusDays(i)) // customize this
                    .courtName("Court " + (i + 1))
                    .round(MatchRound.QUARTERFINAL) // change this dynamically if needed
                    .status(MatchStatus.SCHEDULED)
                    .build();

            Match savedMatch = matchRepository.save(match);

            Score score = Score.builder()
                    .match(savedMatch)
                    .player1_points(0)
                    .player2_points(0)
                    .sets_won_player1(0)
                    .sets_won_player2(0)
                    .tiebreak_played(false)
                    .retirement(false)
                    .walkover(false)
                    .build();

            savedMatch.setScore(score);
            matchRepository.save(savedMatch); // saves both match + score due to cascade
        }
    }
    @Override
    public List<AdminTournamentResponse> getAllTournaments() {
        return tournamentRepository.findAll().stream().map(t -> AdminTournamentResponse.builder()
                .id(t.getId())
                .name(t.getName())
                .location(t.getLocation())
                .startDate(t.getStartDate())
                .endDate(t.getEndDate())
                .registrationDeadline(t.getRegistrationDeadline())
                .build()
        ).toList();
    }


    @Override
    public List<Match> getTournamentMatches(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        return matchRepository.findByTournament(tournament);
    }
    @Override
    public void deleteTournament(Long id) {
        if (!tournamentRepository.existsById(id)) {
            throw new RuntimeException("Tournament not found");
        }
        tournamentRepository.deleteById(id);
    }

    @Override
    public void deleteMatch(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new RuntimeException("Match not found");
        }
        matchRepository.deleteById(id);
    }
    @Override
    public Tournament updateTournament(Long id, Tournament updated) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        tournament.setName(updated.getName());
        tournament.setLocation(updated.getLocation());
        tournament.setStartDate(updated.getStartDate());
        tournament.setEndDate(updated.getEndDate());
        tournament.setRegistrationDeadline(updated.getRegistrationDeadline());

        return tournamentRepository.save(tournament);
    }
    @Override
    public void assignRefereesToTournament(Long tournamentId, List<Long> refereeIds) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        List<User> referees = userRepository.findAllById(refereeIds).stream()
                .filter(user -> user.getRole().name().equals("REFEREE"))
                .toList();

        // If referees set is null (first assignment), initialize it
        if (tournament.getReferees() == null) {
            tournament.setReferees(new HashSet<>());
        }

        tournament.getReferees().addAll(referees); // <-- merge instead of replace
        tournamentRepository.save(tournament);
    }
    @Override
    public byte[] exportTournamentMatchesAsCsv(Long tournamentId) {
        List<Match> matches = getTournamentMatches(tournamentId);
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
    public byte[] exportTournamentMatchesAsTxt(Long tournamentId) {
        List<Match> matches = getTournamentMatches(tournamentId);
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







}