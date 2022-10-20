package hongik.ce.jolup.module.match.event;

import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MatchUpdatedEvent {
    private final Match match;
    private final String message;
    private final List<Member> members;
}
