package hongik.ce.jolup.domain.join;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JoinRole {
    MASTER("관리자"),
    MANAGER("매니저"),
    USER("유저");

    private final String description;
}
