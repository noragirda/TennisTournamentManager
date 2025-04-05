package com.example.tennistournament.controller;

import com.example.tennistournament.dto.RefereeMatchResponse;
import com.example.tennistournament.dto.ScoreUpdateRequest;
import com.example.tennistournament.repository.UserRepository;
import com.example.tennistournament.service.RefereeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/referee")
@PreAuthorize("hasRole('REFEREE')")
public class RefereeController {

    @Autowired
    private RefereeService refereeService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/matches")
    public ResponseEntity<List<RefereeMatchResponse>> getOwnMatches(Principal principal) {
        Long refereeId = getUserIdFromPrincipal(principal);
        List<RefereeMatchResponse> matches = refereeService.getOwnMatches(refereeId);
        return ResponseEntity.ok(matches);
    }

    @PostMapping("/matches/{matchId}/score")
    public ResponseEntity<String> updateMatchScore(@PathVariable Long matchId,
                                                   @RequestBody ScoreUpdateRequest request,
                                                   Principal principal) {
        Long refereeId = getUserIdFromPrincipal(principal);
        refereeService.updateMatchScore(matchId, request, refereeId);
        return ResponseEntity.ok("Score successfully updated.");
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        return userRepository.findByName(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
