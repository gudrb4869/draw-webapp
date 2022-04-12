package hongik.ce.jolup.domain.room;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
    LEAGUE("리그"),
    TOURNAMENT("토너먼트");

    private final String description;
}
