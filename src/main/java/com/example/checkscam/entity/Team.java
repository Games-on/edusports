package com.example.checkscam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "teams", uniqueConstraints = {
        @UniqueConstraint(name = "uk_team_name_tournament", columnNames = {"tournament_id", "name"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "team_color")
    private String teamColor;

    @Column(name = "member_count", nullable = false)
    private int memberCount = 0;

    @ManyToOne
    @JoinColumn(name = "captain_user_id", nullable = false)
    private User captain;

    @Column(name = "contact_info", columnDefinition = "TEXT")
    private String contactInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TeamStatus status = TeamStatus.ACTIVE;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

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

    public enum TeamStatus {
        ACTIVE, ELIMINATED, WITHDRAWN
    }
}