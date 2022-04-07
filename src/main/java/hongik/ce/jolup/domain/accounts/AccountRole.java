package hongik.ce.jolup.domain.accounts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountRole {
    USER("ROLE_USER", "일반사용자"),
    MANAGER("ROLE_MANAGER", "매니저"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
