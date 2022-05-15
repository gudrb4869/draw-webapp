package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.join.Join;
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

    public List<JoinDto> findByMember(MemberDto memberDto) {
        List<Join> joins = joinRepository.findByMember(memberDto.toEntity());
        List<JoinDto> joinDtos = joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
        return joinDtos;
    }

    public List<JoinDto> findByCompetition(CompetitionDto competitionDto) {
        List<Join> joins = joinRepository.findByCompetition(competitionDto.toEntity());
        List<JoinDto> joinDtos = joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
        return joinDtos;
    }

    public JoinDto findOne(MemberDto memberDto, CompetitionDto competitionDto) {
        Optional<Join> optionalJoin = joinRepository.findByMemberAndCompetition(memberDto.toEntity(), competitionDto.toEntity());
        if (optionalJoin.isEmpty())
            return null;
        Join join = optionalJoin.get();
        JoinDto joinDto = join.toDto();
        return joinDto;
    }

    public List<JoinDto> findByCompetitionSort(CompetitionDto competitionDto) {
        List<Join> joins = joinRepository.findByCompetitionSort(competitionDto.toEntity());
        List<JoinDto> joinDtos = joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
        return joinDtos;
    }

    public List<JoinDto> findByCompetitionOrderByJoinRole(CompetitionDto competitionDto) {
        List<Join> joins = joinRepository.findByCompetitionOrderByJoinRoleDesc(competitionDto.toEntity());
        List<JoinDto> joinDtos = joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
        return joinDtos;
    }

    public void deleteJoin(Long id) {
        joinRepository.deleteById(id);
    }
}
