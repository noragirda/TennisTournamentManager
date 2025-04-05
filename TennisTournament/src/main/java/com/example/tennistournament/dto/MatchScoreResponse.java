package com.example.tennistournament.dto;

import lombok.Builder;
import lombok.Data;

// player sees when checking their past or ongoing match results
@Data
@Builder
public class MatchScoreResponse
{
    private Long matchId;
    private String opponent;
    private String result;
    private String score;
}
