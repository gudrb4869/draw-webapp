package hongik.ce.jolup.domain.match;

import hongik.ce.jolup.domain.score.Score;
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
    private Score score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "match_status")
    private MatchStatus matchStatus;

    @Column(name = "match_no")
    private Long matchNo;

    @Builder
    public Match(Long id, Room room, User user1, User user2, Score score, MatchStatus matchStatus, Long matchNo) {
        this.id = id;
        this.room = room;
        this.user1 = user1;
        this.user2 = user2;
        this.score = score;
        this.matchStatus = matchStatus;
        this.matchNo = matchNo;
    }

    public MatchDto toDto() {
        return MatchDto.builder()
                .id(id)
                .roomDto(room.toDto())
                .user1Dto(user1.toDto())
                .user2Dto(user2.toDto())
                .matchStatus(matchStatus)
                .score(score)
                .matchNo(matchNo)
                .build();
    }

    public Match update(Score score, MatchStatus matchStatus) {
        this.score = score;
        this.matchStatus = matchStatus;
        return this;
    }
}
