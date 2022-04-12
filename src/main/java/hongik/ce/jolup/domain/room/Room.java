package hongik.ce.jolup.domain.room;

import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;

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
    private String subject;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(nullable = false)
    private Long memNum;

    @Builder
    public Room(Long id, String subject, RoomType roomType, Long memNum) {
        this.id = id;
        this.subject = subject;
        this.roomType = roomType;
        this.memNum = memNum;
    }

    public Room update(String subject, RoomType roomType, Long memNum) {
        this.subject = subject;
        this.roomType = roomType;
        this.memNum = memNum;
        return this;
    }
}
