package hongik.ce.jolup.domain.join;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Grade {
    ADMIN("admin"),
    MANAGER("manager"),
    USER("user");

    private final String description;
}
