package hongik.ce.jolup.module.match.endpoint.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MatchForm {
    @PositiveOrZero(message = "음수일 수 없습니다.")
    private Integer homeScore;
    @PositiveOrZero(message = "음수일 수 없습니다.")
    private Integer awayScore;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;
}
