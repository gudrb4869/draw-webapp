package hongik.ce.jolup.module.competition.event;

import hongik.ce.jolup.module.competition.domain.entity.Competition;

public class CompetitionCreatedEvent extends CompetitionEvent{
    public CompetitionCreatedEvent(Competition competition) {
        super(competition, "방 '" + competition.getRoom().getTitle() + "'에서 새로운 대회 '" + competition.getTitle() + "'가 생성되었습니다.");
    }
}
