package hongik.ce.jolup.module.room.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "member", "room", "grade"})
@Table(name = "joins", uniqueConstraints = {@UniqueConstraint(columnNames = {"member_id", "room_id"})})
@NamedEntityGraph(
        name = "Join.withAll",
        attributeNodes = {
                @NamedAttributeNode("room"),
                @NamedAttributeNode("member")
        }
)
public class Join extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Grade grade;

    public static Join from(Room room, Member member, Grade grade) {
        Join join = new Join();
        join.room = room;
        join.member = member;
        join.grade = grade;
        return join;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateGrade(Grade grade) {
        this.grade = grade;
    }
}
