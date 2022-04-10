package hongik.ce.jolup.web.dto;

import hongik.ce.jolup.domain.rooms.RoomType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomSaveRequestDto {
    private String subject;
    private RoomType roomType;
    private Long memNum;
}
