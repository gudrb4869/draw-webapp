package hongik.ce.jolup.module.room.event;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Room;

import java.util.Set;

public class RoomInvitedEvent extends RoomEvent {
    public RoomInvitedEvent(Room room) {
        super(room, "새로운 방을 만들었습니다.");
    }
}
