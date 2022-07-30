package hongik.ce.jolup.domain.score;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Embeddable
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Score {
    @Column(nullable = false)
    private Integer homeScore;

    @Column(nullable = false)
    private Integer awayScore;

    @Builder
    public Score(Integer homeScore, Integer awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }
}
