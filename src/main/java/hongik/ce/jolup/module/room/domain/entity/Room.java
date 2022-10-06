package hongik.ce.jolup.module.room.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.endpoint.form.RoomForm;
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

    private boolean access;

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
    public Room(Long id, String name, Member master, boolean access) {
        this.id = id;
        this.name = name;
        this.master = master;
        this.access = access;
    }

    public static Room from(RoomForm roomForm) {
        Room room = new Room();
        room.name = roomForm.getName();
        room.access = roomForm.isAccess();
        return room;
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


    public void updateAccess(boolean access) {
        this.access = access;
    }
}
