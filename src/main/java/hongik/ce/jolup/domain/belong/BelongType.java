package hongik.ce.jolup.domain.belong;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BelongType {
    ADMIN("방장"),
    MANAGER("매니저"),
    USER("일반사용자");

    private final String description;
}
