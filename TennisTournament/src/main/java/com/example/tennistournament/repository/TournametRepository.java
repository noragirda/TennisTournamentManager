package com.example.tennistournament.repository;

import com.example.tennistournament.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournametRepository extends JpaRepository<Tournament, Long>
{
}
