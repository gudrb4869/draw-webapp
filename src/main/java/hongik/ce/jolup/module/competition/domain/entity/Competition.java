package hongik.ce.jolup.module.competition.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.competition.endpoint.form.CompetitionForm;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "title", "type", "room"})
public class Competition extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompetitionType type;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Participate> participates = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Match> matches = new ArrayList<>();

    @Builder
    public Competition(Long id, String title, CompetitionType type, Room room) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.room = room;
    }

    public static Competition from(CompetitionForm competitionForm, Room room) {
        Competition competition = new Competition();
        competition.title = competitionForm.getTitle();
        competition.type = competitionForm.getType();
        competition.room = room;
        return competition;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
