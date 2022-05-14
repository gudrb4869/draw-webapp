package hongik.ce.jolup.domain.score;

import hongik.ce.jolup.dto.ScoreDto;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Embeddable
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Score {
    @Column(nullable = false, name = "home_score")
    private Integer homeScore;

    @Column(nullable = false, name = "away_score")
    private Integer awayScore;

    @Builder
    public Score(Integer homeScore, Integer awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public ScoreDto toDto() {
        return ScoreDto.builder()
                .homeScore(homeScore)
                .awayScore(awayScore)
                .build();
    }
}
