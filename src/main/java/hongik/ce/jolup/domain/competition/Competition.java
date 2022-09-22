package hongik.ce.jolup.domain.competition;

import hongik.ce.jolup.domain.BaseTimeEntity;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.room.Room;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name", "type", "option", "room"})
@EqualsAndHashCode(of = "id")
public class Competition extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_id")
    private Long id;

    @Column(nullable = false)
    private String name;

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

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<League> leagues = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Tournament> tournaments = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<HeadToHead> headToHeads = new ArrayList<>();

    @Builder
    public Competition(Long id, String name, CompetitionType type, CompetitionOption option, Room room) {
        this.id = id;
        this.name = name;
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

    public Competition update(String name, CompetitionType type) {
        this.name = name;
        this.type = type;
        return this;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
