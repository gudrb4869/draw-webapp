package hongik.ce.jolup.module.competition.application;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.competition.infra.repository.CompetitionRepository;
import hongik.ce.jolup.module.competition.infra.repository.ParticipateRepository;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.infra.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipateService {

    private final ParticipateRepository participateRepository;
    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;

    public void createRankingTables(List<Member> memberList, Competition competition) {
        List<Participate> participates = memberList.stream().map(m -> Participate.builder().member(m).competition(competition)
                .win(0).draw(0).lose(0).goalFor(0).goalAgainst(0).build()).collect(Collectors.toList());
        participateRepository.saveAll(participates);
    }

    public void save(List<Long> memberIds, Long leagueId) {
        List<Member> members = memberRepository.findAllById(memberIds);
        Competition competition = competitionRepository.findById(leagueId).orElse(null);
        for (Member member : members) {
            Participate participate = Participate.builder().member(member).competition(competition)
                    .win(0).draw(0).lose(0).goalFor(0).goalAgainst(0).build();
            participateRepository.save(participate);
        }
    }

    public Long save(Long memberId, Long leagueId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        Competition competition = competitionRepository.findById(leagueId).orElse(null);
        if (member != null && competition != null) {
            Participate participate = Participate.builder().member(member).competition(competition)
                    .win(0).draw(0).lose(0).goalFor(0).goalAgainst(0).build();
            return participateRepository.save(participate).getId();
        }
        return null;
    }

    public void delete(Long id) {
        participateRepository.deleteById(id);
    }

    public void setNull(Long memberId) {
        List<Participate> participates = participateRepository.findByMemberId(memberId);
        for (Participate participate : participates) {
            participate.updateMember(null);
        }
    }

    public List<Participate> findByMemberId(Long memberId) {
        return participateRepository.findByMemberId(memberId);
    }

    public List<Participate> findByCompetitionId(Long competitionId) {
        return participateRepository.findByCompetitionId(competitionId);
    }

    public Participate findOne(Long memberId, Long competitionId) {
        return participateRepository.findByMemberIdAndCompetitionId(memberId, competitionId).orElse(null);
    }
}
