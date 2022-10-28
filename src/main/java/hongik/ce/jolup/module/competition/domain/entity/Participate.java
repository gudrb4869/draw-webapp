package hongik.ce.jolup.module.competition.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Participate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Competition competition;

    private int win = 0;

    private int draw = 0;

    private int lose = 0;

    private int goalFor = 0;

    private int goalAgainst = 0;

    public static Participate from(Member member, Competition competition) {
        Participate participate = new Participate();
        participate.member = member;
        participate.competition = competition;
        return participate;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    // 편의 메서드
    public void addWin(int win) {
        this.win += win;
    }

    public void subWin(int win) {
        this.win -= win;
    }

    public void addDraw(int draw) {
        this.draw += draw;
    }

    public void subDraw(int draw) {
        this.draw -= draw;
    }

    public void addLose(int lose) {
        this.lose += lose;
    }

    public void subLose(int lose) {
        this.lose -= lose;
    }

    public void addGoalFor(int goalFor) {
        this.goalFor += goalFor;
    }

    public void subGoalFor(int goalFor) {
        this.goalFor -= goalFor;
    }

    public void addGoalAgainst(int goalAgainst) {
        this.goalAgainst += goalAgainst;
    }

    public void subGoalAgainst(int goalAgainst) {
        this.goalAgainst -= goalAgainst;
    }
}
