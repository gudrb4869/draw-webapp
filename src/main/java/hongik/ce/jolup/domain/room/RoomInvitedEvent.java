package hongik.ce.jolup.domain.room;

import hongik.ce.jolup.domain.member.Member;
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
