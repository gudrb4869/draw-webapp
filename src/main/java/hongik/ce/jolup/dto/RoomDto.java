package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomSetting;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter @NoArgsConstructor
@ToString
public class RoomDto {
    private Long id;

    @NotBlank(message = "방 이름을 입력해주세요!")
    @Size(min = 3, max = 30, message = "최소 3글자 최대 30글자로 입력해주세요!")
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