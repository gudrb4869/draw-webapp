package hongik.ce.jolup.domain.belong;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.dto.BelongDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "belong")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Belong {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "belong_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Builder
    public Belong(Long id, Member member, Room room) {
        this.id = id;
        if (member != null) {
            changeMember(member);
        }
        if (room != null) {
            changeRoom(room);
        }
    }

    public BelongDto toDto() {
        return BelongDto.builder().id(id).memberDto(member.toDto()).roomDto(room.toDto()).build();
    }

    public void changeMember(Member member) {
        this.member = member;
        member.getBelongs().add(this);
    }

    public void changeRoom(Room room) {
        this.room = room;
        room.getBelongs().add(this);
    }
}
