package hongik.ce.jolup.module.room.event;

import hongik.ce.jolup.module.room.domain.entity.Room;

public class RoomInvitedEvent extends RoomEvent {
    public RoomInvitedEvent(Room room) {
        super(room, "방 '" + room.getTitle() + "'에서 회원님을 초대하였습니다.");
    }
}
