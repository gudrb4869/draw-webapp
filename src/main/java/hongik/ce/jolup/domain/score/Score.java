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
    private Integer user1Score;

    @Column(nullable = false, name = "away_score")
    private Integer user2Score;

    @Builder
    public Score(Integer user1Score, Integer user2Score) {
        this.user1Score = user1Score;
        this.user2Score = user2Score;
    }

    public ScoreDto toDto() {
        return ScoreDto.builder()
                .user1Score(user1Score)
                .user2Score(user2Score)
                .build();
    }
}
