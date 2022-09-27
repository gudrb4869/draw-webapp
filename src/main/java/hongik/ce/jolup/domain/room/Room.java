package hongik.ce.jolup.domain.room;

import hongik.ce.jolup.domain.BaseTimeEntity;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.competition.Competition;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name", "access"})
public class Room extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Access access;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Join> joins = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Competition> competitions = new ArrayList<>();

    @Builder
    public Room(Long id, String name, Access access) {
        this.id = id;
        this.name = name;
        this.access = access;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateAccess(Access access) {
        this.access = access;
    }
}
