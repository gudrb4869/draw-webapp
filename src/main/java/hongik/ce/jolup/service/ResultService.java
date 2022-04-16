package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ResultService {
    private final ResultRepository resultRepository;
    private final MatchService matchService;

    public Long save(Long matchId, Short homeScore, Short awayScore) {
        return resultRepository.save(Result.builder()
                .match(matchService.findById(matchId).toEntity())
                .homeScore(homeScore)
                .awayScore(awayScore).build()).getId();
    }
}
