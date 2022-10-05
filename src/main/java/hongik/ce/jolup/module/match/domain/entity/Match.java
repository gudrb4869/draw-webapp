package hongik.ce.jolup.module.match.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.Status;
import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "match_round", "match_number"})})
@Table(name = "matches")
@ToString(of = {"id", "round", "number", "status", "homeScore", "awayScore"})
public class Match extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private Member home;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_id")
    private Member away;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "match_round", nullable = false)
    private Integer round;

    @Column(name = "match_number", nullable = false)
    private Integer number;

    @Column
    private Integer homeScore;

    @Column
    private Integer awayScore;

    @Builder
    public Match(Long id, Competition competition, Member home, Member away, Status status, Integer round, Integer number, Integer homeScore, Integer awayScore) {
        this.id = id;
        this.competition = competition;
        this.home = home;
        this.away = away;
        this.status = status;
        this.round = round;
        this.number = number;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public void updateHome(Member home) {
        this.home = home;
    }

    public void updateAway(Member away) {
        this.away = away;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void updateRound(Integer round) {
        this.round = round;
    }

    public void updateNumber(Integer number) {
        this.number = number;
    }

    public void updateScore(Integer homeScore, Integer awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

}
