package hongik.ce.jolup.module.competition.application;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.competition.endpoint.form.CompetitionForm;
import hongik.ce.jolup.module.competition.event.CompetitionCreatedEvent;
import hongik.ce.jolup.module.competition.infra.repository.CompetitionRepository;
import hongik.ce.jolup.module.competition.infra.repository.ParticipateRepository;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.match.infra.repository.MatchRepository;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompetitionService {

    private final ParticipateRepository participateRepository;
    private final MatchRepository matchRepository;
    private final CompetitionRepository competitionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Competition createCompetition(Room room, CompetitionForm competitionForm) {
        Competition competition = competitionRepository.save(Competition.from(competitionForm, room));

        matchRepository.save(Match.from(competition, null, null, 0, 0));
        return competition;
    }

    public Competition getCompetition(Room room, Long competitionId) {
        Competition competition = competitionRepository.findCompetitionWithRoomAndParticipatesById(competitionId).orElse(null);
        checkExistsCompetition(room, competition);
        return competition;
    }

    private void checkExistsCompetition(Room room, Competition competition) {
        if (competition == null || !competition.getRoom().equals(room)) {
            throw new IllegalArgumentException("존재하지 않는 대회입니다.");
        }
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() > 0 && newTitle.length() <= 50;
    }

    public void updateCompetitionTitle(Competition competition, String newTitle) {
        competition.updateTitle(newTitle);
    }

    public void remove(Competition competition) {
        if (!competition.isRemovable()) {
            throw new IllegalStateException("대회를 삭제할 수 없습니다.");
        }
        competitionRepository.delete(competition);
    }
}
