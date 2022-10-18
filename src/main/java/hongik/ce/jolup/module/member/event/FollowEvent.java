package hongik.ce.jolup.module.member.event;

import hongik.ce.jolup.module.member.domain.entity.Follow;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FollowEvent {

    private final Follow follow;
    private final String message;
}
