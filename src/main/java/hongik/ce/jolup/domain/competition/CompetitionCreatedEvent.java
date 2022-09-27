package hongik.ce.jolup.domain.competition;

import hongik.ce.jolup.domain.member.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class CompetitionCreatedEvent {

    private final Competition competition;
    private final String message;
    private final Set<Member> members;
}
