package hongik.ce.jolup.module.room.event;

import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RoomEvent {
    private final Room room;
    private final String message;
}
