package hongik.ce.jolup.domain.competition;

import hongik.ce.jolup.domain.BaseTimeEntity;
import hongik.ce.jolup.domain.member.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class LeagueGame extends BaseTimeEntity {

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

    @Column(name = "game_round", nullable = false)
    private Integer round;

    @Column
    private Integer homeScore;

    @Column
    private Integer awayScore;

    @Builder
    public LeagueGame(Long id, Competition competition, Member home, Member away, Status status, Integer round, Integer homeScore, Integer awayScore) {
        this.id = id;
        this.competition = competition;
        this.home = home;
        this.away = away;
        this.status = status;
        this.round = round;
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

    public void updateScore(Integer homeScore, Integer awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }
}
