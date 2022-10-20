package hongik.ce.jolup.module.competition.event;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CompetitionCreatedEvent {
    private final Competition competition;
    private final String message;
    private final List<Member> members;
}
