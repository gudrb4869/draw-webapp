package hongik.ce.jolup.module.match.event;

import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class MatchUpdatedEvent {

    private final Match match;
    private final Long roomId;
    private final String message;
    private final Set<Member> members;
}
