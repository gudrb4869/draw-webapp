package hongik.ce.jolup.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberAuth {
    USER("일반사용자"),
    MANAGER( "매니저"),
    ADMIN("관리자");

    private final String description;
}
