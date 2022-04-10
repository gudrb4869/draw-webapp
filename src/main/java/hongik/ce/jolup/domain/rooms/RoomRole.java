package hongik.ce.jolup.domain.rooms;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomRole {
    MASTER,
    MANAGER,
    GUEST
}
