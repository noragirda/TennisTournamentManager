package com.example.tennistournament.model;
import com.example.tennistournament.model.enums.MatchRound;
import com.example.tennistournament.model.enums.MatchStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    @JsonIgnore
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "player1_id")
    private User player1;

    @ManyToOne
    @JoinColumn(name = "player2_id")
    private User player2;

    @ManyToOne
    @JoinColumn(name = "referee_id")
    private User referee;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;

    @Column(name="match_datetime", nullable = false)
    private LocalDateTime matchDatetime;
    private String courtName;

    @Enumerated(EnumType.STRING)
    private MatchRound round;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    private Integer durationMinutes;

    @OneToOne(mappedBy = "match", cascade = CascadeType.ALL)
    @JsonIgnore
    private Score score;


}
