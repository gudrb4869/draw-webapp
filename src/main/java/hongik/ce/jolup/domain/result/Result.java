package hongik.ce.jolup.domain.result;

import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.dto.ResultDto;
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
public class Result {
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
    public Result(/*Long id, Match match, */Integer user1Score, Integer user2Score) {
        /*this.id = id;
        this.match = match;*/
        this.user1Score = user1Score;
        this.user2Score = user2Score;
    }

    public static ResultDto toDto(Result result) {
        return ResultDto.builder()
                /*.id(result.getId())
                .matchDto(Match.toDto(result.getMatch()))*/
                .user1Score(result.getUser1Score())
                .user2Score(result.getUser2Score())
                .build();
    }
}
