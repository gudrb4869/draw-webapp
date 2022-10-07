package hongik.ce.jolup.module.competition.endpoint.form;

import hongik.ce.jolup.module.competition.domain.entity.CompetitionOption;
import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionForm {
    @Pattern(regexp = "^[a-zA-Z가-힣0-9_-]{1,50}$", message = "대회 이름을 1자에서 50자이내로 입력하세요.")
    private String title;

    @NotNull(message = "대회 방식을 선택하세요.")
    private CompetitionType type = CompetitionType.LEAGUE;

    @NotNull(message = "대회 옵션을 선택하세요.")
    private CompetitionOption option = CompetitionOption.SINGLE;

    private Set<Long> members = new HashSet<>();
}
