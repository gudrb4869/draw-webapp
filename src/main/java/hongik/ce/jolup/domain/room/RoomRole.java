package hongik.ce.jolup.domain.room;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomRole {
    MASTER("방관리자"),
    MANAGER("방매니저"),
    GUEST("방유저");

    private final String description;
}
