package hongik.ce.jolup.domain.competition;

import hongik.ce.jolup.domain.BaseTimeEntity;
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
@ToString(of = {"id", "title", "competitionType", "room"})
@EqualsAndHashCode(of = "id")
public class Competition extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_id")
    private Long id;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompetitionType competitionType;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Join> joins = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Match> matches = new ArrayList<>();

    @Builder
    public Competition(Long id, String title, CompetitionType competitionType, Room room) {
        this.id = id;
        this.title = title;
        this.competitionType = competitionType;
        this.room = room;
    }

    /*public CompetitionDto toDto() {
        return CompetitionDto.builder()
                .id(id)
                .title(title)
                .competitionType(competitionType)
                .roomDto(room.toDto())
                .build();
    }*/

    public Competition update(String title, CompetitionType competitionType) {
        this.title = title;
        this.competitionType = competitionType;
        return this;
    }

    public void changeRoom(Room room) {
        this.room = room;
        room.getCompetitions().add(this);
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
