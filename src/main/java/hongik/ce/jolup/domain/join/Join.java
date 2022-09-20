package hongik.ce.jolup.domain.join;

import hongik.ce.jolup.domain.BaseTimeEntity;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.competition.Competition;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "joins")
@ToString
@EqualsAndHashCode(of = "id")
public class Join extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "join_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @Embedded
    private Result result;

    @Builder
    public Join(Long id, Member member, Competition competition, Result result) {
        this.id = id;
        this.member = member;
        this.competition = competition;
        this.result = result;
    }

    /*public JoinDto toDto () {
        return JoinDto.builder()
                .id(id)
                .memberDto(member.toDto())
                .competitionDto(competition.toDto())
                .result(result)
                .build();
    }*/

    public void updateResult(Result result) {
        this.result = result;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public void changeCompetition(Competition competition) {
        this.competition = competition;
        competition.getJoins().add(this);
    }
}
