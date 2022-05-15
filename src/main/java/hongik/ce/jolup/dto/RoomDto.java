package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomSetting;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor
@ToString
public class RoomDto {
    private Long id;

    @NotBlank(message = "방 이름을 입력해주세요!")
    private String title;

    @NotNull(message = "방 공개 여부를 선택해주세요!")
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