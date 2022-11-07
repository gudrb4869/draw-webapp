package hongik.ce.jolup.module.competition.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.account.domain.entity.Account;
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
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private Competition competition;

    private int win = 0;

    private int draw = 0;

    private int lose = 0;

    private int goalFor = 0;

    private int goalAgainst = 0;

    public static Participate from(Account account, Competition competition) {
        Participate participate = new Participate();
        participate.account = account;
        participate.competition = competition;
        return participate;
    }

    public void updateMember(Account account) {
        this.account = account;
    }

    // 편의 메서드

    public void updateGoalFor(int goalFor) {
        this.goalFor += goalFor;
    }

    public void updateGoalAgainst(int goalAgainst) {
        this.goalAgainst += goalAgainst;
    }

    public void addWin() {
        this.win++;
    }

    public void subWin() {
        this.win--;
    }

    public void addDraw() {
        this.draw++;
    }

    public void subDraw() {
        this.draw--;
    }

    public void addLose() {
        this.lose++;
    }

    public void subLose() {
        this.lose--;
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
