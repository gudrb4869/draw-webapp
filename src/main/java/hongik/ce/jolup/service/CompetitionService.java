package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.repository.CompetitionRepository;
import hongik.ce.jolup.dto.CompetitionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    @Transactional
    public Long saveCompetition(CompetitionDto competitionDto) {
        return competitionRepository.save(competitionDto.toEntity()).getId();
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

    public List<CompetitionDto> findAll() {
        return competitionRepository.findAll().stream()
                .map(Competition::toDto)
                .collect(Collectors.toList());
    }

    public CompetitionDto findOne(Long competitionId) {
        Optional<Competition> optionalCompetition = competitionRepository.findById(competitionId);
        if (optionalCompetition.isEmpty()) {
            return null;
        }
        Competition competition = optionalCompetition.get();
        return competition.toDto();
    }

    public List<CompetitionDto> findCompetitions(Long roomId) {
        List<Competition> competitions = competitionRepository.findByRoomId(roomId);
        if (competitions == null) {
            return null;
        }
        return competitions.stream().map(Competition::toDto).collect(Collectors.toList());
    }

    public CompetitionDto findOne(Long competitionId, Long roomId) {
        Optional<Competition> optionalCompetition = competitionRepository.findByIdAndRoomId(competitionId, roomId);
        if (optionalCompetition.isEmpty()) {
            return null;
        }
        Competition competition = optionalCompetition.get();
        return competition.toDto();
    }
}
