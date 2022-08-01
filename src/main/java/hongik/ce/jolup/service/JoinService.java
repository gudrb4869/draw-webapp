package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.repository.CompetitionRepository;
import hongik.ce.jolup.repository.JoinRepository;
import hongik.ce.jolup.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;

    @Transactional
    public void save(List<Long> memberIds, Long competitionId) {
        List<Member> members = memberRepository.findAllById(memberIds);
        Competition competition = competitionRepository.findById(competitionId).orElse(null);
        for (Member member : members) {
            Result result = Result.builder().plays(0).win(0).draw(0).lose(0).goalFor(0)
                    .goalAgainst(0).goalDifference(0).points(0).build();
            Join join = Join.builder().member(member).competition(competition).result(result).build();
            joinRepository.save(join);
        }
    }

    @Transactional
    public Long update() {
        return null;
    }
    
    @Transactional
    public void deleteJoin(Long id) {
        joinRepository.deleteById(id);
    }

    public List<Join> findByMemberId(Long memberId) {
        List<Join> joins = joinRepository.findByMemberId(memberId);
        return joins;
    }

    public List<Join> findByCompetitionId(Long competitionId) {
        List<Join> joins = joinRepository.findByCompetitionId(competitionId);
        return joins;
    }

    public Join findOne(Long memberId, Long competitionId) {
        return joinRepository.findByMemberIdAndCompetitionId(memberId, competitionId).orElse(null);
    }

    public List<Join> findByCompetitionSort(Long competitionId) {
        List<Join> joins = joinRepository.findByCompetitionIdSort(competitionId);
        return joins;
    }
}
