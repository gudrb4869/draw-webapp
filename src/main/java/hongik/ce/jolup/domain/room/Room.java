package hongik.ce.jolup.domain.room;

import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.dto.RoomDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "room")
public class Room {
    /*
    drop table if exists room CASCADE;
    create table room(
    room_id bigint generated by default as identity,
    subject varchar(255) not null,
    room_type enum('LEAGUE', 'TOURNAMENT') not null,
    mem_num bigint not null,
    primary key(room_id)
    );
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(nullable = false)
    private Long memNum;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Join> joins = new ArrayList<>();

    @Builder
    public Room(Long id, String title, RoomType roomType, Long memNum) {
        this.id = id;
        this.title = title;
        this.roomType = roomType;
        this.memNum = memNum;
    }

    public static RoomDto toDto(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .title(room.getTitle())
                .roomType(room.getRoomType())
                .memNum(room.getMemNum())
                .build();
    }

    public Room update(String title, RoomType roomType, Long memNum) {
        this.title = title;
        this.roomType = roomType;
        this.memNum = memNum;
        return this;
    }
}
