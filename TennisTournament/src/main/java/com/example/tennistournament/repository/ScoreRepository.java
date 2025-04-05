package com.example.tennistournament.repository;

import com.example.tennistournament.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, Long>
{
}
