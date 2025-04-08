package com.example.tennistournament.controller;
import com.example.tennistournament.dto.*;
import com.example.tennistournament.dto.MatchTourSchResponse;
import com.example.tennistournament.model.Match;
import com.example.tennistournament.model.User;
import com.example.tennistournament.security.JwtUtil;
import com.example.tennistournament.service.MatchService;
import com.example.tennistournament.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    @GetMapping("/tournament/{tournamentId}/matches")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<List<MatchTourSchResponse>> getMatchesByTournament(@PathVariable Long tournamentId) {
        List<MatchTourSchResponse> matches = matchService.getMatchesByTournament(tournamentId);
        return ResponseEntity.ok(matches);
    }
    @GetMapping("/tournament/{tournamentId}/scores")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<List<MatchTourSchResponse>> getScoresByTournament(@PathVariable Long tournamentId) {
        List<MatchTourSchResponse> matches = matchService.getMatchesByTournament(tournamentId); // reuse same mapping
        return ResponseEntity.ok(matches);
    }




}
