package com.example.tennistournament.dto;

import lombok.Data;

@Data
public class ScoreUpdateRequest {
    private int player1Points;
    private int player2Points;
    private int setsWonPlayer1;
    private int setsWonPlayer2;
    private Long winnerId; // Must be one of the two players
    private boolean tiebreakPlayed;
    private boolean retirement;
    private boolean walkover;
    private String set1;
    private String set2;
    private String set3;
    private String status;


}
