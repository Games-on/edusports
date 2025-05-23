package com.example.checkscam.entity;

import com.example.checkscam.constant.TournamentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sport_type", nullable = false)
    private String sportType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_teams", nullable = false)
    private int maxTeams;

    @Column(name = "current_teams", nullable = false)
    private int currentTeams = 0;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TournamentStatus status = TournamentStatus.REGISTRATION;

    @ManyToOne
    @JoinColumn(name = "winner_team_id")
    private Team winnerTeam;

    @ManyToOne
    @JoinColumn(name = "runner_up_team_id")
    private Team runnerUpTeam;

    @Column(name = "rules", columnDefinition = "TEXT")
    private String rules;

    @Column(name = "prize_info", columnDefinition = "TEXT")
    private String prizeInfo;

    @Column(name = "contact_info", columnDefinition = "TEXT")
    private String contactInfo;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "last_updated_at")
    private Long lastUpdatedAt;

    @ManyToOne
    @JoinColumn(name = "last_updated_by")
    private User lastUpdatedBy;


}
