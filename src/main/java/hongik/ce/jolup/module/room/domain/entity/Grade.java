package hongik.ce.jolup.module.room.domain.entity;

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
