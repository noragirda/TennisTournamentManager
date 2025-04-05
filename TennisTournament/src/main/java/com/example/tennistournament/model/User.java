package com.example.tennistournament.model;


import com.example.tennistournament.model.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
@Entity
@Table(name="users")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private Set<Registration> tournamentRegistrations;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


}
