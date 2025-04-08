package com.example.tennistournament.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RefereeMatchResponse {
    private Long matchId;
    private String player1Name;
    private Long player1Id;
    private String player2Name;
    private Long player2Id;
    private LocalDateTime matchDateTime;
    private String round;
    private String location;
    private String status;
    private String tournamentName;
}
