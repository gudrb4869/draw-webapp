package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.score.Score;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreDto {
    private Integer homeScore;
    private Integer awayScore;

    public Score toEntity() {
        return Score.builder()
                .homeScore(homeScore)
                .awayScore(awayScore).build();
    }
}