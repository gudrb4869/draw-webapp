package hongik.ce.jolup.module.match.endpoint.form;

import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.match.domain.entity.Status;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MatchForm {

    private String home;
    @Min(0)
    @Max(100)
    private Integer homeScore = 0;
    @Min(0)
    @Max(100)
    private Integer awayScore = 0;
    private String away;
    @NotNull
    private Status status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    public static Object from(Match match) {
        MatchForm matchForm = new MatchForm();
        if (match.getHome() != null) {
            matchForm.home = match.getHome().getName();
        }
        if (match.getHomeScore() != null) {
            matchForm.homeScore = match.getHomeScore();
        }
        if (match.getAwayScore() != null) {
            matchForm.awayScore = match.getAwayScore();
        }
        if (match.getAway() != null) {
            matchForm.away = match.getAway().getName();
        }
        return matchForm;
    }
}
