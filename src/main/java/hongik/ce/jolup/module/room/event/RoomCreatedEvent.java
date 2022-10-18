package hongik.ce.jolup.module.room.event;

import hongik.ce.jolup.module.room.domain.entity.Room;

public class RoomCreatedEvent extends RoomEvent {
    public RoomCreatedEvent(Room room) {
        super(room, "새로운 방 '" + room.getTitle() + "'가 생성되었습니다.");
    }
}
