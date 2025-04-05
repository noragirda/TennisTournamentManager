package com.example.tennistournament.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RefereeMatchResponse {
    private Long matchId;
    private String player1;
    private String player2;
    private LocalDateTime matchDateTime;
    private String round;
    private String location;
    private String status;
}
