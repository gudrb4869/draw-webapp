package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter @Setter @ToString
@NoArgsConstructor
public class BelongDto {
    private Long id;
    private MemberDto memberDto;
    private RoomDto roomDto;
    @NotNull(message = "회원 권한을 선택해주세요!")
    private BelongType belongType;

    @Builder
    public BelongDto(Long id, MemberDto memberDto, RoomDto roomDto, BelongType belongType) {
        this.id = id;
        this.memberDto = memberDto;
        this.roomDto = roomDto;
        this.belongType = belongType;
    }

    public Belong toEntity() {
        return Belong.builder().id(id).member(memberDto.toEntity()).room(roomDto.toEntity()).belongType(belongType).build();
    }
}
