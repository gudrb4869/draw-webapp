package hongik.ce.jolup.module.competition.event;

import hongik.ce.jolup.module.competition.domain.entity.Competition;

public class MatchUpdatedEvent extends CompetitionEvent {
    public MatchUpdatedEvent(Competition competition) {
        super(competition, "경기 결과가 수정되었습니다.");
    }
}
