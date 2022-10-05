package hongik.ce.jolup.module.notification.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    ROOM_INVITED,
    COMPETITION_CREATED,
    MATCH_UPDATED;
}
