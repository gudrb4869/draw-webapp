package hongik.ce.jolup.domain.room;

import hongik.ce.jolup.domain.Time;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends Time {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Builder
    public Room(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
