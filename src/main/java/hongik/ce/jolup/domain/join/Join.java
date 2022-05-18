package hongik.ce.jolup.domain.join;

import hongik.ce.jolup.domain.BaseTimeEntity;
import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.dto.JoinDto;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "joins")
@ToString
public class Join extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belong_id")
    private Belong belong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @Embedded
    private Result result;

    @Builder
    public Join(Long id, Belong belong, Competition competition, Result result) {
        this.id = id;
        if (belong != null) {
            changeBelong(belong);
        }
        if (competition != null) {
            changeCompetition(competition);
        }
        this.result = result;
    }

    public JoinDto toDto () {
        return JoinDto.builder()
                .id(id)
                .belongDto(belong.toDto())
                .competitionDto(competition.toDto())
                .result(result)
                .build();
    }

    public Join update(Result result) {
        this.result = result;
        return this;
    }

    public void changeBelong(Belong belong) {
        this.belong = belong;
        belong.getJoins().add(this);
    }

    public void changeCompetition(Competition competition) {
        this.competition = competition;
        competition.getJoins().add(this);
    }
}
