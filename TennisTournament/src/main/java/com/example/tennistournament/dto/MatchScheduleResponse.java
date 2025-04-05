package com.example.tennistournament.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

//what a player sees when he checks his upcoming matches
@Data
@Builder
public class MatchScheduleResponse {
    private Long matchId;
    private String opponent;
    private LocalDateTime matchDateTime;
    private String round;
    private String location;
}