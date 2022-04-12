package hongik.ce.jolup.web.dto;

import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomType;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
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

    @Builder
    public RoomDto(Long id, String subject, RoomType roomType, Long memNum) {
        this.id = id;
        this.subject = subject;
        this.roomType = roomType;
        this.memNum = memNum;
    }
}
