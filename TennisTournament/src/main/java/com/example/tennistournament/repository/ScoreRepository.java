package com.example.tennistournament.repository;

import com.example.tennistournament.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    Optional<Score> findByMatchId(Long matchId);
}
