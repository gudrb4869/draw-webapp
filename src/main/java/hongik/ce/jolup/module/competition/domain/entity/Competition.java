package hongik.ce.jolup.module.competition.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name", "type", "room"})
public class Competition extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompetitionType type;

    /*@Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompetitionOption option;*/

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<LeagueTable> leagueTables = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Match> matches = new ArrayList<>();

    @Builder
    public Competition(Long id, String name, CompetitionType type/*, CompetitionOption option*/, Room room) {
        this.id = id;
        this.name = name;
        this.type = type;
//        this.option = option;
        this.room = room;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
