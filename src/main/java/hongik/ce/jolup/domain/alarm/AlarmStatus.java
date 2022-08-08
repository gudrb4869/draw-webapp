package hongik.ce.jolup.domain.alarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmStatus {

    BEFORE("읽지 않음"),
    AFTER("읽음");

    private final String description;
}
