package com.example.tennistournament.service;

import com.example.tennistournament.model.Match;
import com.example.tennistournament.model.Score;
import com.example.tennistournament.model.User;

import java.util.List;

public interface PlayerTournamentService
{
    void registerToTournament(Long playerId ,Long tournamentId);
    List<Match> getPlayerSchedule(Long playerId);
    List<Score> getPlayerMatchScores(Long playerId);
    List<Match> getPlayerMatches(User player);
    Match getMatchById(Long matchId);

}
