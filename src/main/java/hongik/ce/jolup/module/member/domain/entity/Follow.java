package hongik.ce.jolup.module.member.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"following_id", "follower_id"})})
@ToString
public class Follow extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member following;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member follower;

    public static Follow from(Member following, Member follower) {
        Follow follow = new Follow();
        follow.following = following;
        follow.follower = follower;
        return follow;
    }
}
