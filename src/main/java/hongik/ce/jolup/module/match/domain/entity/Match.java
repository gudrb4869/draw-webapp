package hongik.ce.jolup.module.match.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.match.endpoint.form.ScoreForm;
import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "match_round", "match_number"})})
@Table(name = "matches")
@ToString(of = {"id", "round", "number", "status", "homeScore", "awayScore"})
@NamedEntityGraph(
        name = "Match.withCompetitionAndRoomAndHomeAndAway",
        attributeNodes = {
                @NamedAttributeNode(value = "competition", subgraph = "room"),
                @NamedAttributeNode("home"),
                @NamedAttributeNode("away")
        },
        subgraphs = @NamedSubgraph(name = "room", attributeNodes = @NamedAttributeNode("room"))
)
public class Match extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member home;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member away;

    private boolean status;

    @Column(name = "match_round")
    private Integer round;

    @Column(name = "match_number")
    private Integer number;

    private Integer homeScore = 0;

    private Integer awayScore = 0;

    private LocalDateTime startDateTime;

    private boolean closed;

    public static Match from(Competition competition, Member home, Member away, int round, int number) {
        Match match = new Match();
        match.competition = competition;
        match.home = home;
        match.away = away;
        match.round = round;
        match.number = number;
        return match;
    }

    public void updateHome(Member home) {
        this.home = home;
    }

    public void updateAway(Member away) {
        this.away = away;
    }

    public void updateScoreFrom(ScoreForm scoreForm) {
        this.homeScore = scoreForm.getHomeScore();
        this.awayScore = scoreForm.getAwayScore();
        this.status = scoreForm.isStatus();
    }

    public void reset() {
        this.homeScore = 0;
        this.awayScore = 0;
        this.status = false;
    }

    public void open() {
        this.closed = false;
    }

    public void close() {
        this.closed = true;
    }

    public void updateStartDateTime(LocalDateTime newStartDateTime) {
        this.startDateTime = newStartDateTime;
    }
}
