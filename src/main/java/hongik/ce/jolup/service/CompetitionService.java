package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.CompetitionCreatedEvent;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.repository.CompetitionRepository;
import hongik.ce.jolup.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final RoomRepository roomRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Competition save(String name, CompetitionType type, Long roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        Competition competition = Competition.builder().name(name).type(type).room(room).build();
        return competitionRepository.save(competition);
    }

    public void SendAlarm(Competition competition, Set<Member> members) {
        eventPublisher.publishEvent(new CompetitionCreatedEvent(competition, "새로운 대회가 생성되었습니다.", members));
    }

    @Transactional
    public void deleteCompetition(Long id) {
        competitionRepository.deleteById(id);
    }

    @Transactional
    public Long updateCompetition(Long id, String name) {
        Competition competition = competitionRepository.findById(id).orElse(null);
        if (competition == null) {
            return null;
        }
        competition.updateName(name);
        return competition.getId();
    }

    public List<Competition> findAll() {
        return competitionRepository.findAll();
    }

    public Competition findOne(Long competitionId) {
        return competitionRepository.findById(competitionId).orElse(null);
    }

    public Page<Competition> findCompetitions(Long roomId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        return competitionRepository.findByRoomId(roomId, PageRequest.of(page, 9, Sort.by("createdDate").descending()));
    }

    public Competition findOne(Long competitionId, Long roomId) {
        return competitionRepository.findByIdAndRoomId(competitionId, roomId).orElse(null);
    }
}
