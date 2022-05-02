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
    private UserDto user1Dto;
    private UserDto user2Dto;
    private Score score;
    private MatchStatus matchStatus;
    private Long matchNo;
    private Long roundNo;

    public Match toEntity() {
        return Match.builder()
                .id(id)
                .room(roomDto.toEntity())
                .user1(user1Dto == null ? null : user1Dto.toEntity())
                .user2(user2Dto == null ? null : user2Dto.toEntity())
                .score(score)
                .matchStatus(matchStatus)
                .matchNo(matchNo)
                .roundNo(roundNo)
                .build();
    }
}
