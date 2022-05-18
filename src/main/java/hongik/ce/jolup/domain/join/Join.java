package hongik.ce.jolup.domain.join;

import hongik.ce.jolup.domain.Time;
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
@ToString(of = {"id", "belong", "competition", "joinRole", "result"})
public class Join extends Time {
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JoinRole joinRole;

    @Builder
    public Join(Long id, Belong belong, Competition competition, Result result, JoinRole joinRole) {
        this.id = id;
        if (belong != null) {
            changeBelong(belong);
        }
        if (competition != null) {
            changeCompetition(competition);
        }
        this.result = result;
        this.joinRole = joinRole;
    }

    public JoinDto toDto () {
        return JoinDto.builder()
                .id(id)
                .belongDto(belong.toDto())
                .competitionDto(competition.toDto())
                .result(result)
                .joinRole(joinRole)
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
