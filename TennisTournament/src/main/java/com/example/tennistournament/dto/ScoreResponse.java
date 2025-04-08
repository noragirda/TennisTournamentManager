package com.example.tennistournament.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScoreResponse
{
    private int player1Points;
    private int player2Points;
    private int setsWonPlayer1;
    private int setsWonPlayer2;
    private String set1;
    private String set2;
    private String set3;
    private boolean tiebreakPlayed;
    private boolean retirement;
    private boolean walkover;
}
