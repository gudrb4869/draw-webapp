package hongik.ce.jolup.domain.room;

import hongik.ce.jolup.domain.BaseTimeEntity;
import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.competition.Competition;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "title", "roomSetting"})
@EqualsAndHashCode(of = "id")
public class Room extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomSetting roomSetting;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Belong> belongs = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Competition> competitions = new ArrayList<>();

    @Builder
    public Room(Long id, String name, RoomSetting roomSetting) {
        this.id = id;
        this.name = name;
        this.roomSetting = roomSetting;
    }

    /*public RoomDto toDto() {
        return RoomDto.builder().id(id).title(title).roomSetting(roomSetting).build();
    }*/

    public void updateName(String name) {
        this.name = name;
    }

    public void updateRoomSetting(RoomSetting roomSetting) {
        this.roomSetting = roomSetting;
    }
}
