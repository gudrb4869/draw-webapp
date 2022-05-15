package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.dto.MatchDto;
import hongik.ce.jolup.dto.CompetitionDto;
import hongik.ce.jolup.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;

    public Long saveMatch(MatchDto matchDto) {
        return matchRepository.save(matchDto.toEntity()).getId();
    }

    public List<MatchDto> findByCompetition(CompetitionDto competitionDto) {
        List<Match> matches = matchRepository.findByCompetition(competitionDto.toEntity());
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

    public MatchDto findOne(CompetitionDto competitionDto, Integer roundNo, Integer matchNo) {
        Optional<Match> optionalMatch = matchRepository.findByCompetitionAndRoundNoAndMatchNo(competitionDto.toEntity(), roundNo, matchNo);
        if (optionalMatch.isEmpty()) {
            return null;
        }
        Match match = optionalMatch.get();
        MatchDto matchDto = match.toDto();
        return matchDto;
    }

    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }

}