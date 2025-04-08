package com.example.tennistournament.repository;

import com.example.tennistournament.model.Registration;
import com.example.tennistournament.model.Tournament;
import com.example.tennistournament.model.User;
import com.example.tennistournament.model.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration,Long>
{
    boolean existsByPlayerIdAndTournamentId(Long playerId, Long tournamentId);
    List<Registration> findByTournamentAndStatus(Tournament tournament, RegistrationStatus registrationStatus) ;
    @Query("SELECT r FROM Registration r WHERE r.player.id = :playerId AND r.status = 'APPROVED'")
    List<Registration> findApprovedRegistrationsByPlayerId(@Param("playerId") Long playerId);
    @Query("SELECT r FROM Registration r WHERE r.tournament.id = :tournamentId AND r.status = :status")
    List<Registration> findApprovedRegistrationsByTournamentId(
            @Param("tournamentId") Long tournamentId,
            @Param("status") RegistrationStatus status
    );
}
