package hongik.ce.jolup.module.competition.endpoint.form;

import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class CompetitionForm {
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;

    @NotNull(message = "대회 방식을 선택하세요.")
    private CompetitionType type = CompetitionType.SINGLE_ROUND_ROBIN;

    private Set<Long> members = new HashSet<>();
}
