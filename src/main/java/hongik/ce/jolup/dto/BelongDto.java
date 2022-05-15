package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.belong.Belong;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor
public class BelongDto {
    private Long id;
    private MemberDto memberDto;
    private RoomDto roomDto;

    @Builder
    public BelongDto(Long id, MemberDto memberDto, RoomDto roomDto) {
        this.id = id;
        this.memberDto = memberDto;
        this.roomDto = roomDto;
    }

    public Belong toEntity() {
        return Belong.builder().id(id).member(memberDto.toEntity()).room(roomDto.toEntity()).build();
    }
}
