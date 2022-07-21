package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomSetting;
import lombok.*;

@Getter @Setter @NoArgsConstructor
@ToString
public class RoomDto {

    private Long id;
    private String title;
    private RoomSetting roomSetting;

    @Builder
    public RoomDto(Long id, String title, RoomSetting roomSetting) {
        this.id = id;
        this.title = title;
        this.roomSetting = roomSetting;
    }

    public Room toEntity() {
        return Room.builder().id(id).title(title).roomSetting(roomSetting).build();
    }
}