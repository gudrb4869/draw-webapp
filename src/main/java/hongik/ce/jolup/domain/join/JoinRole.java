package hongik.ce.jolup.domain.join;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JoinRole {
    MASTER("방관리자"),
    GUEST("방유저");

    private final String description;
}
