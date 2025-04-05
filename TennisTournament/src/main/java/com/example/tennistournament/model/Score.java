package com.example.tennistournament.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "match_id", unique = true)
    private Match match;

    private int player1_points;
    private int player2_points;
    private int sets_won_player1;
    private int sets_won_player2;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner_id;

    private boolean tiebreak_played;
    private boolean retirement;
    private boolean walkover;

    public String getScoreValue() {
        if (retirement) return "Retired";
        if (walkover) return "Walkover";

        StringBuilder scoreBuilder = new StringBuilder();
        scoreBuilder.append("Sets: ")
                .append(sets_won_player1)
                .append("-")
                .append(sets_won_player2);

        if (tiebreak_played) {
            scoreBuilder.append(" (Tiebreak)");
        }

        scoreBuilder.append(" | Points: ")
                .append(player1_points)
                .append("-")
                .append(player2_points);

        return scoreBuilder.toString();
    }

}
