package com.example.tennistournament.service;

import com.example.tennistournament.dto.AdminTournamentResponse;
import com.example.tennistournament.dto.AdminUpdateUserRequest;
import com.example.tennistournament.dto.UserResponse;
import com.example.tennistournament.model.Match;
import com.example.tennistournament.model.Tournament;

import java.util.List;

public interface AdminService
{
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long userId);
    void updateUser(Long userId, AdminUpdateUserRequest request);
    void deleteUser(Long userId);
    byte[] exportMatchListAsCsv();
    byte[] exportMatchListAsTxt();
    Tournament createTournament(Tournament tournament);
    void generateMatches(Long tournamentId);
    List<AdminTournamentResponse> getAllTournaments();
    List<Match> getTournamentMatches(Long tournamentId);
    void deleteTournament(Long id);
    void deleteMatch(Long id);
    Tournament updateTournament(Long id, Tournament updated);
    void assignRefereesToTournament(Long tournamentId, List<Long> refereeIds);
    byte[] exportTournamentMatchesAsCsv(Long tournamentId);
    byte[] exportTournamentMatchesAsTxt(Long tournamentId);




}
