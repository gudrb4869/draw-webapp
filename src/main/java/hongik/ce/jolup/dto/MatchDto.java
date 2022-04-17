package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.result.Result;
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
    private Result result;

    public Match toEntity() {
        return Match.builder()
                .id(id)
                .room(roomDto.toEntity())
                .user1(user1Dto.toEntity())
                .user2(user2Dto.toEntity())
                .result(result)
                .build();
    }
}
