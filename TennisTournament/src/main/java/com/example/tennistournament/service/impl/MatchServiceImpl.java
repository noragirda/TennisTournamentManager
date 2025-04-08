package com.example.tennistournament.service.impl;

import com.example.tennistournament.dto.MatchScheduleResponse;
import com.example.tennistournament.dto.MatchTourSchResponse;
import com.example.tennistournament.model.Match;
import com.example.tennistournament.model.Tournament;
import com.example.tennistournament.repository.MatchRepository;
import com.example.tennistournament.repository.TournametRepository;
import com.example.tennistournament.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {

    @Autowired
    private TournametRepository tournamentRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Override
    public List<MatchTourSchResponse> getMatchesByTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        List<Match> matches = matchRepository.findByTournament(tournament);

        return matches.stream()
                .map(match -> MatchTourSchResponse.builder()
                        .id(match.getId())
                        .player1Name(match.getPlayer1().getName())
                        .player2Name(match.getPlayer2().getName())
                        .refereeName(match.getReferee() != null ? match.getReferee().getName() : "TBD")
                        .scheduledDate(match.getMatchDatetime())
                        .courtName(match.getCourtName())
                        .status(match.getStatus().name())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
