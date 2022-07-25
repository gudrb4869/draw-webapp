package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BelongDto {
    private Long id;
    private MemberDto memberDto;
    private RoomDto roomDto;
    private BelongType belongType;

    public Belong toEntity() {
        return Belong.builder().id(id).member(memberDto.toEntity()).room(roomDto.toEntity()).belongType(belongType).build();
    }
}
