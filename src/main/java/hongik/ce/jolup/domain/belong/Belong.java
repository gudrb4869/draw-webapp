package hongik.ce.jolup.domain.belong;

import hongik.ce.jolup.domain.BaseTimeEntity;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.dto.BelongDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "belongType"})
@EqualsAndHashCode(of = "id")
public class Belong extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "belong_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BelongType belongType;

    @OneToMany(mappedBy = "belong", cascade = CascadeType.ALL)
    private List<Join> joins = new ArrayList<>();

    @Builder
    public Belong(Long id, Member member, Room room, BelongType belongType) {
        this.id = id;
        if (member != null) {
            changeMember(member);
        }
        if (room != null) {
            changeRoom(room);
        }
        this.belongType = belongType;
    }

    public BelongDto toDto() {
        return BelongDto.builder().id(id).memberDto(member.toDto()).roomDto(room.toDto()).belongType(belongType).build();
    }

    public void changeMember(Member member) {
        this.member = member;
        member.getBelongs().add(this);
    }

    public void changeRoom(Room room) {
        this.room = room;
        room.getBelongs().add(this);
    }

    public void changeBelongType(BelongType belongType) {
        this.belongType = belongType;
    }
}
