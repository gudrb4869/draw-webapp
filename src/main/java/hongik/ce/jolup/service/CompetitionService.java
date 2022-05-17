package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.repository.CompetitionRepository;
import hongik.ce.jolup.dto.CompetitionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    public Long saveCompetition(CompetitionDto competitionDto) {
        return competitionRepository.save(competitionDto.toEntity()).getId();
    }

    public List<CompetitionDto> findAll() {
        return competitionRepository.findAll().stream()
                .map(Competition::toDto)
                .collect(Collectors.toList());
    }

    public CompetitionDto getCompetition(Long competitionId) {
        Optional<Competition> competitionWrapper = competitionRepository.findById(competitionId);
        if (competitionWrapper.isEmpty()) {
            return null;
        }
        Competition competition = competitionWrapper.get();
        log.info("competition.joins = {}", competition.getJoins());
        return competition.toDto();
    }

    public List<CompetitionDto> getCompetitions(Long roomId) {
        List<Competition> competitions = competitionRepository.findByRoomId(roomId);
        if (competitions == null) {
            return null;
        }
        return competitions.stream().map(Competition::toDto).collect(Collectors.toList());
    }

    public void deleteCompetition(Long id) {
        competitionRepository.deleteById(id);
    }
}
