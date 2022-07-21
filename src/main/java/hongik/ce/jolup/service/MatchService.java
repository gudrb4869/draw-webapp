package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.dto.MatchDto;
import hongik.ce.jolup.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;

    @Transactional
    public Long saveMatch(MatchDto matchDto) {
        return matchRepository.save(matchDto.toEntity()).getId();
    }

    public List<MatchDto> findByCompetition(Long competitionId) {
        List<Match> matches = matchRepository.findByCompetitionId(competitionId);
        List<MatchDto> matchDtos = matches.stream()
                .map(Match::toDto)
                .collect(Collectors.toList());
        return matchDtos;
    }

    public MatchDto getMatch(Long id) {
        Optional<Match> matchWrapper = matchRepository.findById(id);
        if (matchWrapper.isEmpty()) {
            return null;
        }
        Match match = matchWrapper.get();
        MatchDto matchDto = match.toDto();
        return matchDto;
    }

    public MatchDto findByIdAndCompetitionId(Long id, Long competitionId) {
        Optional<Match> optionalMatch = matchRepository.findByIdAndCompetitionId(id, competitionId);
        if (optionalMatch.isEmpty())
            return null;
        Match match = optionalMatch.get();
        return match.toDto();
    }

    public MatchDto findOne(Long competitionId, Integer roundNo, Integer matchNo) {
        Optional<Match> optionalMatch = matchRepository.findByCompetitionIdAndRoundNoAndMatchNo(competitionId, roundNo, matchNo);
        if (optionalMatch.isEmpty()) {
            return null;
        }
        Match match = optionalMatch.get();
        MatchDto matchDto = match.toDto();
        return matchDto;
    }

    @Transactional
    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }

}