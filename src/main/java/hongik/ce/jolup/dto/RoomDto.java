package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomType;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;
    private String title;
    private RoomType roomType;
    private Long headCount;
//    private List<JoinDto> joinDtos;

    public Room toEntity() {
        return Room.builder()
                .id(id)
                .title(title)
                .roomType(roomType)
                .headCount(headCount)
//                .joins(joinDtos.stream().map(JoinDto::toEntity).collect(Collectors.toList()))
                .build();
    }
}
