package hongik.ce.jolup.domain.match;

import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.dto.MatchDto;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_id")
    private User user2;

    @Builder
    public Match(Long id, Room room, User user1, User user2) {
        this.id = id;
        this.room = room;
        this.user1 = user1;
        this.user2 = user2;
    }

    public static MatchDto toDto(Match match) {
        return MatchDto.builder()
                .id(match.getId())
                .roomDto(Room.toDto(match.getRoom()))
                .user1Dto(User.toDto(match.getUser1()))
                .user2Dto(User.toDto(match.getUser2()))
                .build();
    }
}
