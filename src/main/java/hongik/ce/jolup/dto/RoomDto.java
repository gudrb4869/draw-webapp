package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.room.Room;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor
@ToString
public class RoomDto {
    private Long id;

    @NotBlank(message = "방 이름을 입력해주세요!")
    private String title;

    @Builder
    public RoomDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Room toEntity() {
        return Room.builder().id(id).title(title).build();
    }
}