package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.score.Score;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MatchDto {
    private Long id;
    private RoomDto roomDto;
    private MemberDto homeDto;
    private MemberDto awayDto;
    private Score score;
    private MatchStatus matchStatus;
    private Integer roundNo;
    private Integer matchNo;

    public Match toEntity() {
        return Match.builder()
                .id(id)
                .room(roomDto.toEntity())
                .home(homeDto == null ? null : homeDto.toEntity())
                .away(awayDto == null ? null : awayDto.toEntity())
                .score(score)
                .matchStatus(matchStatus)
                .roundNo(roundNo)
                .matchNo(matchNo)
                .build();
    }
}
