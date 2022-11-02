package hongik.ce.jolup.module.competition.endpoint.form;

import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class CompetitionForm {
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @NotNull(message = "대회 방식을 선택하세요.")
    private CompetitionType type = CompetitionType.ROUND_ROBIN;

    private Set<Long> members = new HashSet<>();

    public boolean isLeague() {
        return this.type.equals(CompetitionType.ROUND_ROBIN);
    }

    public boolean isTournament() {
        return this.type.equals(CompetitionType.SINGLE_ELIMINATION_TOURNAMENT) || this.type.equals(CompetitionType.DOUBLE_ELIMINATION_TOURNAMENT);
    }
}
