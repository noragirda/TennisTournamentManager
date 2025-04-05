package com.example.tennistournament.repository;

import com.example.tennistournament.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration,Long>
{
    boolean existsByPlayerIdAndTournamentId(Long playerId, Long tournamentId);
}
