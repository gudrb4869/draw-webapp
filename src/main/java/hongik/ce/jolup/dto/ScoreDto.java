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
    private Integer user1Score;
    private Integer user2Score;

    public Score toEntity() {
        return Score.builder()
                .user1Score(user1Score)
                .user2Score(user2Score).build();
    }
}