package hongik.ce.jolup.module.room.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Access {
    PRIVATE("비공개"),
    PUBLIC("공개");

    private final String description;
}
