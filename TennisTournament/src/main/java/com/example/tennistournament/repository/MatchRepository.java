package com.example.tennistournament.repository;

import com.example.tennistournament.model.Match;
import com.example.tennistournament.model.Tournament;
import com.example.tennistournament.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> 
{

    List<Match> findByPlayer1OrPlayer2(User player, User player1);
    List<Match> findByReferee(User referee);
    List<Match> findByTournament(Tournament tournament);

}
