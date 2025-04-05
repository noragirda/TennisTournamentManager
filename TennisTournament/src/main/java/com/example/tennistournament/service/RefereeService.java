package com.example.tennistournament.service;

import com.example.tennistournament.dto.RefereeMatchResponse;
import com.example.tennistournament.dto.ScoreUpdateRequest;

import java.util.List;

public interface RefereeService
{
    List<RefereeMatchResponse> getOwnMatches(Long refereeIs);
    void updateMatchScore(Long matchId, ScoreUpdateRequest request, Long refereeId);
}
