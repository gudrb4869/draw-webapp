package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.CompetitionOption;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.repository.CompetitionRepository;
import hongik.ce.jolup.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public Long save(String title, CompetitionType type, CompetitionOption option, Long roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        Competition competition = Competition.builder().title(title).type(type).option(option).room(room).build();
        return competitionRepository.save(competition).getId();
    }

    @Transactional
    public void deleteCompetition(Long id) {
        competitionRepository.deleteById(id);
    }

    @Transactional
    public Long updateCompetition(Long id, String title) {
        Competition competition = competitionRepository.findById(id).orElse(null);
        if (competition == null) {
            return null;
        }
        competition.updateTitle(title);
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
        return competitionRepository.findByRoomId(roomId, PageRequest.of(page, 3, Sort.by("createdDate").descending()));
    }

    public Competition findOne(Long competitionId, Long roomId) {
        return competitionRepository.findByIdAndRoomId(competitionId, roomId).orElse(null);
    }
}
