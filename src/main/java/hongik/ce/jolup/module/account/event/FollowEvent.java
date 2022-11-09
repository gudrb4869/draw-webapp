package hongik.ce.jolup.module.account.event;

import hongik.ce.jolup.module.account.domain.entity.Follow;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class FollowEvent {

    private final Follow follow;
    private final String message;
}
