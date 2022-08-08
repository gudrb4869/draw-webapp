package hongik.ce.jolup.domain.alarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {

    ROOM_INVITE("방에 초대"),
    COMPETITION_INVITE("대회에 초대");

    private final String description;
}
