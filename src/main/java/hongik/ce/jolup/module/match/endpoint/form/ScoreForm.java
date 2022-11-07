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

    @Min(0)
    @Max(999)
    private Integer homeScore = 0;
    @Min(0)
    @Max(999)
    private Integer awayScore = 0;

    @NotNull
    private boolean finished;

    public static ScoreForm from(Match match) {
        ScoreForm scoreForm = new ScoreForm();
        scoreForm.homeScore = match.getHomeScore();
        scoreForm.awayScore = match.getAwayScore();
        return scoreForm;
    }
}
