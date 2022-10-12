package hongik.ce.jolup.module.competition.event;

import hongik.ce.jolup.module.competition.domain.entity.Competition;

public class CompetitionCreatedEvent extends CompetitionEvent{
    public CompetitionCreatedEvent(Competition competition) {
        super(competition, "새로운 대회가 생성되었습니다.");
    }
}
