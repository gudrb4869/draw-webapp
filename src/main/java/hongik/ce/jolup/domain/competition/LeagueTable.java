package hongik.ce.jolup.domain.competition;

import hongik.ce.jolup.domain.BaseTimeEntity;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.member.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class LeagueTable extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @Column(nullable = false)
    private Integer win;

    @Column(nullable = false)
    private Integer draw;

    @Column(nullable = false)
    private Integer lose;

    @Column(nullable = false)
    private Integer goalFor;

    @Column(nullable = false)
    private Integer goalAgainst;

    @Builder
    public LeagueTable(Long id, Member member, Competition competition, Integer win, Integer draw, Integer lose, Integer goalFor, Integer goalAgainst) {
        this.id = id;
        this.member = member;
        this.competition = competition;
        this.win = win;
        this.draw = draw;
        this.lose = lose;
        this.goalFor = goalFor;
        this.goalAgainst = goalAgainst;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    // 편의 메서드
    public void addWin(Integer win) {
        this.win += win;
    }

    public void subWin(Integer win) {
        this.win -= win;
    }

    public void addDraw(Integer draw) {
        this.draw += draw;
    }

    public void subDraw(Integer draw) {
        this.draw -= draw;
    }

    public void addLose(Integer lose) {
        this.lose += lose;
    }

    public void subLose(Integer lose) {
        this.lose -= lose;
    }

    public void addGoalFor(Integer goalFor) {
        this.goalFor += goalFor;
    }

    public void subGoalFor(Integer goalFor) {
        this.goalFor -= goalFor;
    }

    public void addGoalAgainst(Integer goalAgainst) {
        this.goalAgainst += goalAgainst;
    }

    public void subGoalAgainst(Integer goalAgainst) {
        this.goalAgainst -= goalAgainst;
    }
}
