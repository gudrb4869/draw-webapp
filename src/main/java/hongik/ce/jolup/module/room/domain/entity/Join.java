package hongik.ce.jolup.module.room.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.account.domain.entity.Account;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "joins", uniqueConstraints = {@UniqueConstraint(columnNames = {"account_id", "room_id"})})
@NamedEntityGraph(
        name = "Join.withAll",
        attributeNodes = {
                @NamedAttributeNode("room"),
                @NamedAttributeNode("account")
        }
)
public class Join extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Grade grade;

    public static Join from(Room room, Account account, Grade grade) {
        Join join = new Join();
        join.room = room;
        join.account = account;
        join.grade = grade;
        return join;
    }

    public void updateMember(Account account) {
        this.account = account;
    }

    public void updateGrade(Grade grade) {
        this.grade = grade;
    }
}
