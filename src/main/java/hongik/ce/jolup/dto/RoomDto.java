package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomType;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;
    private String subject;
    private RoomType roomType;
    private Long memNum;

    public Room toEntity() {
        Room room = Room.builder()
                .id(id)
                .subject(subject)
                .roomType(roomType)
                .memNum(memNum)
                .build();
        return room;
    }
}
