package hongik.ce.jolup.module.match.endpoint.form;

import hongik.ce.jolup.module.match.domain.entity.Match;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ScoreForm {


    private String home;
    @Min(0)
    @Max(100)
    private Integer homeScore = 0;
    @Min(0)
    @Max(100)
    private Integer awayScore = 0;
    private String away;

    @NotNull
    private boolean status;

    /*@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;*/

    public static ScoreForm from(Match match) {
        ScoreForm scoreForm = new ScoreForm();
        if (match.getHome() != null) {
            scoreForm.home = match.getHome().getName();
        }
        if (match.getHomeScore() != null) {
            scoreForm.homeScore = match.getHomeScore();
        }
        if (match.getAwayScore() != null) {
            scoreForm.awayScore = match.getAwayScore();
        }
        if (match.getAway() != null) {
            scoreForm.away = match.getAway().getName();
        }
        scoreForm.status = match.isStatus();
//        scoreForm.startDateTime = match.getStartDateTime();
        return scoreForm;
    }
}
