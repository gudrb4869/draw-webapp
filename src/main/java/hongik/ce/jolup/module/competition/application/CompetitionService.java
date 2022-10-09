package hongik.ce.jolup.module.competition.application;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.endpoint.form.CompetitionForm;
import hongik.ce.jolup.module.competition.event.CompetitionCreatedEvent;
import hongik.ce.jolup.module.competition.infra.repository.CompetitionRepository;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Competition createCompetition(Room room, CompetitionForm competitionForm) {
        Competition competition = Competition.from(competitionForm, room);
//        eventPublisher.publishEvent(new RoomUpdatedEvent(competition.getRoom(), "'" + competition.getTitle() + "' 대회가 생성되었습니다."));
        return competitionRepository.save(competition);
    }

    public void SendAlarm(Competition competition, Set<Member> members) {
        eventPublisher.publishEvent(new CompetitionCreatedEvent(competition, "새로운 대회가 생성되었습니다.", members));
    }

    public Competition findOne(Long competitionId) {
        return competitionRepository.findById(competitionId).orElse(null);
    }

    public Competition findOne(Long competitionId, Long roomId) {
        return competitionRepository.findByIdAndRoomId(competitionId, roomId).orElse(null);
    }

    public Competition getCompetition(Room room, Long competitionId) {
        Competition competition = competitionRepository.findCompetitionById(competitionId).orElse(null);
        checkExistsCompetition(room, competition);
        return competition;
    }

    public Competition getCompetitionWithMatch(Room room, Long competitionId) {
        Competition competition = competitionRepository.findCompetitionWithMatchesById(competitionId).orElse(null);
        checkExistsCompetition(room, competition);
        return competition;
    }

    private void checkExistsCompetition(Room room, Competition competition) {
        if (competition == null || !competition.getRoom().equals(room)) {
            throw new IllegalArgumentException("존재하지 않는 대회입니다.");
        }
    }
}
