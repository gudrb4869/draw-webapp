package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomSetting;
import lombok.*;

@Getter @Setter @NoArgsConstructor
@ToString @AllArgsConstructor @Builder
public class RoomDto {

    private Long id;
    private String title;
    private RoomSetting roomSetting;

    public Room toEntity() {
        return Room.builder().id(id).title(title).roomSetting(roomSetting).build();
    }
}