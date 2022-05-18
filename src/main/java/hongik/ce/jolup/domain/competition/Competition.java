package hongik.ce.jolup.domain.competition;

import hongik.ce.jolup.domain.Time;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.dto.CompetitionDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "competition")
@ToString(of = {"id", "title", "competitionType", "headCount"})
public class Competition extends Time {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompetitionType competitionType;

    @Column(nullable = false)
    private Long headCount;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Join> joins = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Match> matches = new ArrayList<>();

    @Builder
    public Competition(Long id, String title, CompetitionType competitionType, Long headCount, Room room) {
        this.id = id;
        this.title = title;
        this.competitionType = competitionType;
        this.headCount = headCount;
        if (room != null) {
            changeRoom(room);
        }
    }

    public CompetitionDto toDto() {
        return CompetitionDto.builder()
                .id(id)
                .title(title)
                .competitionType(competitionType)
                .headCount(headCount)
                .roomDto(room.toDto())
                .build();
    }

    public Competition update(String title, CompetitionType competitionType, Long headCount) {
        this.title = title;
        this.competitionType = competitionType;
        this.headCount = headCount;
        return this;
    }

    public void changeRoom(Room room) {
        this.room = room;
        room.getCompetitions().add(this);
    }
}
