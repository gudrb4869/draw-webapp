package hongik.ce.jolup.domain.match;

import hongik.ce.jolup.domain.Time;
import hongik.ce.jolup.domain.score.Score;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.dto.MatchDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "matches")
@ToString
public class Match extends Time {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
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

    @Embedded
    private Score score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "match_status")
    private MatchStatus matchStatus;

    @Column(name = "round_no", nullable = false)
    private Integer roundNo;

    @Column(name = "match_no", nullable = false)
    private Integer matchNo;

    @Builder
    public Match(Long id, Competition competition, Member home, Member away, Score score, MatchStatus matchStatus, Integer roundNo, Integer matchNo) {
        this.id = id;
        this.competition = competition;
        this.home = home;
        this.away = away;
        this.score = score;
        this.matchStatus = matchStatus;
        this.roundNo = roundNo;
        this.matchNo = matchNo;
    }

    public MatchDto toDto() {
        return MatchDto.builder()
                .id(id)
                .competitionDto(competition.toDto())
                .homeDto(home==null?null:home.toDto())
                .awayDto(away==null?null:away.toDto())
                .matchStatus(matchStatus)
                .score(score)
                .roundNo(roundNo)
                .matchNo(matchNo)
                .build();
    }

    public Match update(Score score, MatchStatus matchStatus) {
        this.score = score;
        this.matchStatus = matchStatus;
        return this;
    }
}
