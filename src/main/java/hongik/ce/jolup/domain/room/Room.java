package hongik.ce.jolup.domain.room;

import hongik.ce.jolup.domain.Time;
import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.dto.RoomDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Room extends Time {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomSetting roomSetting;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Belong> belongs = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Competition> competitions = new ArrayList<>();

    @Builder
    public Room(Long id, String title, RoomSetting roomSetting) {
        this.id = id;
        this.title = title;
        this.roomSetting = roomSetting;
    }

    public RoomDto toDto() {
        return RoomDto.builder().id(id).title(title).roomSetting(roomSetting).build();
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateRoomSetting(RoomSetting roomSetting) {
        this.roomSetting = roomSetting;
    }
}
