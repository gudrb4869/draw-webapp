package hongik.ce.jolup.domain.result;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Result {

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