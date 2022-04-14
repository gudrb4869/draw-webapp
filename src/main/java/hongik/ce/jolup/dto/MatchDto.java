package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.user.User;
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

    public Match toEntity(MatchDto matchDto) {
        return Match.builder()
                .id(id)
                .room(roomDto.toEntity())
                .user1(user1Dto.toEntity())
                .user2(user2Dto.toEntity())
                .build();
    }
}
