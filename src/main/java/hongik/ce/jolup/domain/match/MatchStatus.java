package hongik.ce.jolup.domain.match;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchStatus {
    READY("준비"),
    END("종료");

    private final String description;
}
