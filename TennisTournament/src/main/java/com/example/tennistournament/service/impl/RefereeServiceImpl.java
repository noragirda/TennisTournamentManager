package com.example.tennistournament.service.impl;

import com.example.tennistournament.dto.RefereeMatchResponse;
import com.example.tennistournament.dto.ScoreUpdateRequest;
import com.example.tennistournament.model.Match;
import com.example.tennistournament.model.Score;
import com.example.tennistournament.model.User;
import com.example.tennistournament.model.enums.MatchStatus;
import com.example.tennistournament.repository.MatchRepository;
import com.example.tennistournament.repository.ScoreRepository;
import com.example.tennistournament.repository.UserRepository;
import com.example.tennistournament.service.RefereeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefereeServiceImpl implements RefereeService {

    @Autowired private MatchRepository matchRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ScoreRepository scoreRepository;

    @Override
    public List<RefereeMatchResponse> getOwnMatches(Long refereeId) {
        User referee = userRepository.findById(refereeId)
                .orElseThrow(() -> new RuntimeException("Referee not found"));

        List<Match> matches = matchRepository.findByReferee(referee);

        return matches.stream().map(match -> RefereeMatchResponse.builder()
                .matchId(match.getId())
                .player1(match.getPlayer1().getName())
                .player2(match.getPlayer2().getName())
                .matchDateTime(match.getMatchDatetime())
                .round(match.getRound().name())
                .location(match.getCourtName())
                .status(match.getStatus().name())
                .build()).toList();
    }

    @Override
    public void updateMatchScore(Long matchId, ScoreUpdateRequest request, Long refereeId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        if (!match.getReferee().getId().equals(refereeId)) {
            throw new RuntimeException("You are not the assigned referee for this match");
        }

        if (match.getStatus().equals(MatchStatus.COMPLETED) ) {
            throw new RuntimeException("Match score has already been recorded");
        }

        // Validate that winner is one of the players
        if (!request.getWinnerId().equals(match.getPlayer1().getId()) &&
                !request.getWinnerId().equals(match.getPlayer2().getId())) {
            throw new RuntimeException("Winner must be one of the players");
        }

        Score score = Score.builder()
                .match(match)
                .player1_points(request.getPlayer1Points())
                .player2_points(request.getPlayer2Points())
                .sets_won_player1(request.getSetsWonPlayer1())
                .sets_won_player2(request.getSetsWonPlayer2())
                .winner_id(userRepository.findById(request.getWinnerId())
                        .orElseThrow(() -> new RuntimeException("Winner user not found")))
                .tiebreak_played(request.isTiebreakPlayed())
                .retirement(request.isRetirement())
                .walkover(request.isWalkover())
                .build();

        match.setScore(score);
        match.setStatus(MatchStatus.COMPLETED);
        scoreRepository.save(score);
        matchRepository.save(match);
    }
}
