package hongik.ce.jolup.module.room.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "member", "room", "grade"})
@Table(name = "joins")
@NamedEntityGraph(
        name = "Join.withAll",
        attributeNodes = {
                @NamedAttributeNode("room"),
                @NamedAttributeNode("member")
        }
)
public class Join extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "join_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Builder
    public Join(Long id, Member member, Room room, Grade grade) {
        this.id = id;
        this.member = member;
        this.room = room;
        this.grade = grade;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateGrade(Grade grade) {
        this.grade = grade;
    }
}
