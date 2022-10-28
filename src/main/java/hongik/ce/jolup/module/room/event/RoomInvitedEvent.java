package hongik.ce.jolup.module.room.event;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class RoomInvitedEvent {
    private final Room room;
    private final String message;
    private final Set<Member> members;
}
