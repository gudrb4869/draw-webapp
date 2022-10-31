package hongik.ce.jolup.module.room.event;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.Getter;

import java.util.Set;

@Getter
public class RoomInviteEvent extends RoomEvent {
    private final Set<Member> members;

    public RoomInviteEvent(Room room, String message, Set<Member> members) {
        super(room, message);
        this.members = members;
    }
}
