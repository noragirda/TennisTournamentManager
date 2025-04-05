package com.example.tennistournament.service.impl;

import com.example.tennistournament.model.*;
import com.example.tennistournament.repository.MatchRepository;
import com.example.tennistournament.repository.RegistrationRepository;
import com.example.tennistournament.repository.TournametRepository;
import com.example.tennistournament.repository.UserRepository;
import com.example.tennistournament.service.PlayerTournamentService;
import com.example.tennistournament.model.enums.RegistrationStatus;

import com.fasterxml.classmate.AnnotationOverrides;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class PlayerTournamentServiceImpl implements PlayerTournamentService
{
    private UserRepository userRepository;
    private TournametRepository tournametRepository;
    private MatchRepository matchRepository;

    @Autowired
    public PlayerTournamentServiceImpl(RegistrationRepository registrationRepository, UserRepository userRepository, TournametRepository tournametRepository, MatchRepository matchRepository) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.tournametRepository = tournametRepository;
        this.matchRepository = matchRepository;
    }

    private RegistrationRepository registrationRepository;

    @Override
    public void registerToTournament(Long playerId, Long tournamentId)
    {
        User player=userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        Tournament tournament= tournametRepository.findById(tournamentId)
                .orElseThrow(()-> new RuntimeException("Tournament not found"));
        //cheching if it is already registered
        boolean alredayRegistered=registrationRepository.existsByPlayerIdAndTournamentId(playerId, tournamentId);
        if(alredayRegistered)
            throw new RuntimeException("Player alreday registered in this tournamnt");
        Registration registration = Registration.builder()
                .player(player)
                .tournament(tournament)
                .status(RegistrationStatus.CONFIRMED)
                .registrationDate(LocalDate.now())
                .build();

        registrationRepository.save(registration);

    }
    @Override
    public List<Match> getPlayerSchedule(Long playerId) {
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        return matchRepository.findByPlayer1OrPlayer2(player, player);
    }

    @Override
    public List<Score> getPlayerMatchScores(Long playerId) {
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        List<Match> matches = matchRepository.findByPlayer1OrPlayer2(player, player);
        return matches.stream()
                .map(Match::getScore)
                .filter(score -> score != null)
                .toList();
    }
}
