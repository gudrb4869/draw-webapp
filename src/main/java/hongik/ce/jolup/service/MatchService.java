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
    public Long saveMatch(Match match) {
        return matchRepository.save(match).getId();
    }

    @Transactional
    public Long updateMatch(Long id) {
        Match match = matchRepository.findById(id).orElse(null);
        if (match == null) {
            return null;
        }
        return match.getId();
    }
    
    @Transactional
    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }

    public List<Match> findByCompetition(Long competitionId) {
        List<Match> matches = matchRepository.findByCompetitionId(competitionId);
        return matches;
    }

    public Match findByIdAndCompetitionId(Long id, Long competitionId) {
        return matchRepository.findByIdAndCompetitionId(id, competitionId).orElse(null);
    }

    public Match findOne(Long matchId) {
        return matchRepository.findById(matchId).orElse(null);
    }

    public Match findOne(Long competitionId, Integer roundNo, Integer matchNo) {
        return matchRepository.findByCompetitionIdAndRoundNoAndMatchNo(competitionId, roundNo, matchNo).orElse(null);
    }
}