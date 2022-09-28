package hongik.ce.jolup.domain.match;

import hongik.ce.jolup.domain.member.Member;
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
