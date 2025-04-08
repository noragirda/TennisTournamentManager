package com.example.tennistournament.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MatchTourSchResponse {
    private Long id;
    private String player1Name;
    private String player2Name;
    private String refereeName;
    private LocalDateTime scheduledDate;
    private String courtName;
    private String status;

}
