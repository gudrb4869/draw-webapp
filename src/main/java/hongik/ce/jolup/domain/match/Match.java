/*
package hongik.ce.jolup.domain.match;

import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long id;

    @ManyToOne*/
/*(fetch = FetchType.LAZY)*//*

    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne*/
/*(fetch = FetchType.LAZY)*//*

    @JoinColumn(name = "user_id")
    private User user1;

    @ManyToOne*/
/*(fetch = FetchType.LAZY)*//*

    @JoinColumn(name = "user_id")
    private User user2;
}
*/
