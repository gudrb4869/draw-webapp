package hongik.ce.jolup.module.competition.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.competition.endpoint.form.CompetitionForm;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "title", "type", "room"})
@NamedEntityGraph(
        name = "Competition.withRoomAndParticipatesAndMembers",
        attributeNodes = {
                @NamedAttributeNode("room"),
                @NamedAttributeNode(value = "participates", subgraph = "member")
        },
        subgraphs = @NamedSubgraph(name = "member", attributeNodes = @NamedAttributeNode("member"))
)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Competition extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompetitionType type;

    @ColumnDefault(value = "0")
    private Integer count = 0;

    private boolean option;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Participate> participates = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Match> matches = new ArrayList<>();

    public static Competition from(CompetitionForm competitionForm, Room room) {
        Competition competition = new Competition();
        competition.title = competitionForm.getTitle();
        competition.type = competitionForm.getType();
        competition.room = room;
        competition.count = competitionForm.getMembers().size();
        return competition;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public boolean isLeague() {
        return this.type.equals(CompetitionType.ROUND_ROBIN);
    }

    public boolean isTournament() {
        return this.type.equals(CompetitionType.SINGLE_ELIMINATION_TOURNAMENT) || this.type.equals(CompetitionType.DOUBLE_ELIMINATION_TOURNAMENT);
    }

    public boolean isRemovable() {
        return true;
    }

    public void optionOn() {
        this.option = true;
    }

    public void optionOff() {
        this.option = false;
    }
}
