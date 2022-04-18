package hongik.ce.jolup.domain.match;

import hongik.ce.jolup.domain.result.Result;
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

    @Embedded
    private Result result;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "match_status")
    private MatchStatus matchStatus;

    @Builder
    public Match(Long id, Room room, User user1, User user2, Result result, MatchStatus matchStatus) {
        this.id = id;
        this.room = room;
        this.user1 = user1;
        this.user2 = user2;
        this.result = result;
        this.matchStatus = matchStatus;
    }

    public static MatchDto toDto(Match match) {
        return MatchDto.builder()
                .id(match.getId())
                .roomDto(Room.toDto(match.getRoom()))
                .user1Dto(User.toDto(match.getUser1()))
                .user2Dto(User.toDto(match.getUser2()))
                .result(match.getResult())
                .build();
    }

    public Match update(Result result, MatchStatus matchStatus) {
        this.result = result;
        this.matchStatus = matchStatus;
        return this;
    }
}
