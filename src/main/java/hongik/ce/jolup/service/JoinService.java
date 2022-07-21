package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.repository.JoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class JoinService {

    private final JoinRepository joinRepository;

    @Transactional
    public Long saveJoin(JoinDto joinDto) {
        return joinRepository.save(joinDto.toEntity()).getId();
    }

    public List<JoinDto> findByBelong(Long belongId) {
        List<Join> joins = joinRepository.findByBelongId(belongId);
        return joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
    }

    public List<JoinDto> findByCompetition(Long competitionId) {
        List<Join> joins = joinRepository.findByCompetitionId(competitionId);
        return joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
    }

    public JoinDto findOne(Long belongId, Long competitionId) {
        Optional<Join> optionalJoin = joinRepository.findByBelongIdAndCompetitionId(belongId, competitionId);
        if (optionalJoin.isEmpty())
            return null;
        Join join = optionalJoin.get();
        JoinDto joinDto = join.toDto();
        return joinDto;
    }

    public List<JoinDto> findByCompetitionSort(Long competitionId) {
        List<Join> joins = joinRepository.findByCompetitionIdSort(competitionId);
        return joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteJoin(Long id) {
        joinRepository.deleteById(id);
    }
}
