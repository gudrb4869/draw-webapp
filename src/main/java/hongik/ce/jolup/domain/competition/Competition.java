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
@ToString(of = {"id", "title", "type", "option", "room"})
@EqualsAndHashCode(of = "id")
public class Competition extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompetitionType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompetitionOption option;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Join> joins = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Match> matches = new ArrayList<>();

    @Builder
    public Competition(Long id, String title, CompetitionType type, CompetitionOption option, Room room) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.option = option;
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

    public Competition update(String title, CompetitionType type) {
        this.title = title;
        this.type = type;
        return this;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
