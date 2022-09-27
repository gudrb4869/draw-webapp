package hongik.ce.jolup.domain.room;

import hongik.ce.jolup.domain.BaseTimeEntity;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.member.Member;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private Member master;

    @ColumnDefault(value = "0")
    private Integer count = 0;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Join> joins = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Competition> competitions = new ArrayList<>();

    @Builder
    public Room(Long id, String name, Member master, Access access) {
        this.id = id;
        this.name = name;
        this.master = master;
        this.access = access;
    }

    public void updateMaster(Member master) {
        this.master = master;
    }

    public void addCount() {
        this.count++;
    }

    public void subCount() {
        this.count--;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateAccess(Access access) {
        this.access = access;
    }
}
