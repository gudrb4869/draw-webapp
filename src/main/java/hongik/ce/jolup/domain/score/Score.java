package hongik.ce.jolup.domain.score;

import hongik.ce.jolup.dto.ScoreDto;
import lombok.*;

import javax.persistence.*;

/*@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "result")*/
@Getter
@Setter
@Embeddable
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Score {
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "match_id")
    private Match match;*/

    @Column(nullable = false, name = "home_score")
    private Integer user1Score;

    @Column(nullable = false, name = "away_score")
    private Integer user2Score;

    @Builder
    public Score(/*Long id, Match match, */Integer user1Score, Integer user2Score) {
        /*this.id = id;
        this.match = match;*/
        this.user1Score = user1Score;
        this.user2Score = user2Score;
    }

    public static ScoreDto toDto(Score score) {
        return ScoreDto.builder()
                /*.id(result.getId())
                .matchDto(Match.toDto(result.getMatch()))*/
                .user1Score(score.getUser1Score())
                .user2Score(score.getUser2Score())
                .build();
    }
}
