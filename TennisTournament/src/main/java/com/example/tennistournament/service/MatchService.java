package com.example.tennistournament.service;


import com.example.tennistournament.dto.MatchTourSchResponse;

import java.util.List;

public interface MatchService {
    public List<MatchTourSchResponse> getMatchesByTournament(Long tournamentId);
}
