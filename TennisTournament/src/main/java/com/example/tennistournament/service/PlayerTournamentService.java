package com.example.tennistournament.service;

import com.example.tennistournament.model.Match;
import com.example.tennistournament.model.Score;

import java.util.List;

public interface PlayerTournamentService
{
    void registerToTournament(Long playerId ,Long tournamentId);
    List<Match> getPlayerSchedule(Long playerId);
    List<Score> getPlayerMatchScores(Long playerId);

}
