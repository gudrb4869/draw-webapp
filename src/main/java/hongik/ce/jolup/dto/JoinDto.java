package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.join.Join;
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
    private UserDto userDto;
    private RoomDto roomDto;
    private Result result;

    public Join toEntity() {
        return Join.builder()
                .id(id)
                .user(userDto.toEntity())
                .room(roomDto.toEntity())
                .result(result)
                .build();
    }
}
