package hongik.ce.jolup.domain.result;

import hongik.ce.jolup.domain.match.Match;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "result")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @Column(nullable = false)
    private Short homeScore;

    @Column(nullable = false)
    private Short awayScore;

    @Builder
    public Result(Long id, Match match, Short homeScore, Short awayScore) {
        this.id = id;
        this.match = match;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public static void toDto(Result result) {
        ;
    }
}
