/*
package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.dto.ResultDto;
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

    public Long save(Long matchId, Integer user1Score, Integer user2Score) {
        return resultRepository.save(Result.builder()
                .match(matchService.findById(matchId).toEntity())
                .user1Score(user1Score)
                .user2Score(user2Score).build()).getId();
    }

    public ResultDto findOne(Long id) {
        Result result = resultRepository.findById(id).get();
        return Result.toDto(result);
    }
}
*/
