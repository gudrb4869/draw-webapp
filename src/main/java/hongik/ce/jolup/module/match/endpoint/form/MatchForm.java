package hongik.ce.jolup.module.match.endpoint.form;

import hongik.ce.jolup.module.match.domain.entity.Match;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchForm {

    private String home = "";
    @Min(0)
    @Max(100)
    private Integer homeScore;
    @Min(0)
    @Max(100)
    private Integer awayScore;
    private String away = "";

    public static Object from(Match match) {
        MatchForm matchForm = new MatchForm();
        if (match.getHome() != null) {
            matchForm.home = match.getHome().getName();
        }
        matchForm.homeScore = match.getHomeScore();
        matchForm.awayScore = match.getAwayScore();
        if (match.getAway() != null) {
            matchForm.away = match.getAway().getName();
        }
        return matchForm;
    }
}
