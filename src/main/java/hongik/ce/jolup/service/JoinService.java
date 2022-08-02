package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.repository.CompetitionRepository;
import hongik.ce.jolup.repository.JoinRepository;
import hongik.ce.jolup.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            Result result = Result.builder().win(0).draw(0).lose(0).goalFor(0).goalAgainst(0).build();
            Join join = Join.builder().member(member).competition(competition).result(result).build();
            joinRepository.save(join);
        }
    }

    @Transactional
    public void update(Long memberId, Long competitionId, Result result) {
        Join join = joinRepository.findByMemberIdAndCompetitionId(memberId, competitionId).orElse(null);
        if (join == null) {
            return;
        }
        join.updateResult(result);

    }
    
    @Transactional
    public void deleteJoin(Long id) {
        joinRepository.deleteById(id);
    }

    public List<Join> findByMemberId(Long memberId) {
        return joinRepository.findByMemberId(memberId);
    }

    public List<Join> findByCompetitionId(Long competitionId) {
        return joinRepository.findByCompetitionId(competitionId);
    }

    public Join findOne(Long memberId, Long competitionId) {
        return joinRepository.findByMemberIdAndCompetitionId(memberId, competitionId).orElse(null);
    }

    public List<Join> findByCompetitionSort(Long competitionId) {
        return joinRepository.findByCompetitionIdSort(competitionId);
    }
}
