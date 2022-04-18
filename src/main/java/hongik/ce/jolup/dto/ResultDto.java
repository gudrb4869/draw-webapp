package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.result.Result;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultDto {
    /*private Long id;
    private MatchDto matchDto;*/
    private Integer user1Score;
    private Integer user2Score;

    public Result toEntity() {
        return Result.builder()
                /*.id(id)
                .match(matchDto.toEntity())*/
                .user1Score(user1Score)
                .user2Score(user2Score).build();
    }
}