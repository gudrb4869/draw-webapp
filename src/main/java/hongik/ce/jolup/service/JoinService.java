package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.dto.BelongDto;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.CompetitionDto;
import hongik.ce.jolup.dto.MemberDto;
import hongik.ce.jolup.repository.JoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class JoinService {

    private final JoinRepository joinRepository;

    public Long saveJoin(JoinDto joinDto) {
        return joinRepository.save(joinDto.toEntity()).getId();
    }

    public List<JoinDto> findByBelong(BelongDto belongDto) {
        List<Join> joins = joinRepository.findByBelongId(belongDto.getId());
        return joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
    }

    public List<JoinDto> findByCompetition(CompetitionDto competitionDto) {
        List<Join> joins = joinRepository.findByCompetitionId(competitionDto.getId());
        return joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
    }

    public JoinDto findOne(BelongDto belongDto, CompetitionDto competitionDto) {
        Optional<Join> optionalJoin = joinRepository.findByBelongIdAndCompetitionId(belongDto.getId(), competitionDto.getId());
        if (optionalJoin.isEmpty())
            return null;
        Join join = optionalJoin.get();
        JoinDto joinDto = join.toDto();
        return joinDto;
    }

    public List<JoinDto> findByCompetitionSort(CompetitionDto competitionDto) {
        List<Join> joins = joinRepository.findByCompetitionIdSort(competitionDto.getId());
        return joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
    }

    public List<JoinDto> findByCompetitionOrderByJoinRole(CompetitionDto competitionDto) {
        List<Join> joins = joinRepository.findByCompetitionIdOrderByJoinRoleDesc(competitionDto.getId());
        return joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
    }

    public void deleteJoin(Long id) {
        joinRepository.deleteById(id);
    }
}
