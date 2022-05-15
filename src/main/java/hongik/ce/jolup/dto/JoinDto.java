package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.join.JoinRole;
import hongik.ce.jolup.domain.result.Result;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinDto {
    private Long id;
    private MemberDto memberDto;
    private CompetitionDto competitionDto;
    private Result result;
    private JoinRole joinRole;

    public Join toEntity() {
        return Join.builder()
                .id(id)
                .member(memberDto.toEntity())
                .competition(competitionDto.toEntity())
                .result(result)
                .joinRole(joinRole)
                .build();
    }
}
