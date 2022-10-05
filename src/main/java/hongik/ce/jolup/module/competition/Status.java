package hongik.ce.jolup.module.competition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    BEFORE("경기시작전"),
    ONGOING("경기진행중"),
    AFTER("경기종료");

    private final String description;
}
