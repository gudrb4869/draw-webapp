package hongik.ce.jolup.module.match.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.match.endpoint.form.LocationForm;
import hongik.ce.jolup.module.match.endpoint.form.MatchForm;
import hongik.ce.jolup.module.match.endpoint.form.ScoreForm;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "match_round", "match_number"})})
@Table(name = "matches")
@ToString
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

    @ManyToOne(fetch = FetchType.LAZY) @ToString.Exclude
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY) @ToString.Exclude
    private Account home;

    @ManyToOne(fetch = FetchType.LAZY) @ToString.Exclude
    private Account away;

    @Column(name = "match_round")
    private int round;

    @Column(name = "match_number")
    private int number;

    private int homeScore;

    private int awayScore;

    private LocalDateTime startDateTime;

    private boolean closed;

    private boolean finished;

    private Double longitude;

    private Double latitude;

    private String jibunAddress;

    private String roadAddress;

    public static Match from(Competition competition, Account home, Account away, int round, int number) {
        Match match = new Match();
        match.competition = competition;
        match.home = home;
        match.away = away;
        match.round = round;
        match.number = number;
        return match;
    }

    public void updateHome(Account home) {
        this.home = home;
    }

    public void updateAway(Account away) {
        this.away = away;
    }

    public void updateScoreFrom(ScoreForm scoreForm) {
        this.homeScore = scoreForm.getHomeScore();
        this.awayScore = scoreForm.getAwayScore();
        this.finished = scoreForm.isFinished();
    }

    public void updateStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void updateLocation(LocationForm locationForm) {
        this.longitude = locationForm.getLongitude();
        this.latitude = locationForm.getLatitude();
        this.jibunAddress = locationForm.getJibunAddress();
        this.roadAddress = locationForm.getRoadAddress();
    }

    public void updateFrom(MatchForm matchForm) {
        this.homeScore = matchForm.getHomeScore();
        this.awayScore = matchForm.getAwayScore();
        this.startDateTime = matchForm.getStartDateTime();
    }

    public void open() {
        this.closed = false;
    }

    public void close() {
        this.closed = true;
    }

    public void reset() {
        this.homeScore = 0;
        this.awayScore = 0;
        this.finished = false;
    }
}
