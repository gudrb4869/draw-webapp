package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.join.Join;
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

    public Join toEntity() {
        return Join.builder()
                .id(id)
                .user(userDto.toEntity())
                .room(roomDto.toEntity())
                .build();
    }
}
