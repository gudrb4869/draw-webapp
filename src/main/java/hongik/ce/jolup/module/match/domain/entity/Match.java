package hongik.ce.jolup.module.match.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.match.endpoint.form.LocationForm;
import hongik.ce.jolup.module.match.endpoint.form.MatchForm;
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
    private Member home;

    @ManyToOne(fetch = FetchType.LAZY) @ToString.Exclude
    private Member away;

    private boolean status;

    @Column(name = "match_round")
    private int round;

    @Column(name = "match_number")
    private int number;

    private int homeScore = 0;

    private int awayScore = 0;

    private LocalDateTime startDateTime;

    private Double longitude;

    private Double latitude;

    private String jibunAddress;

    private String roadAddress;

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
}
