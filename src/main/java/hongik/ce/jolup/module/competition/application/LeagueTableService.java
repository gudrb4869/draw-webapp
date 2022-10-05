package hongik.ce.jolup.module.competition.application;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.LeagueTable;
import hongik.ce.jolup.module.competition.infra.repository.CompetitionRepository;
import hongik.ce.jolup.module.competition.infra.repository.LeagueTableRepository;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.infra.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeagueTableService {

    private final LeagueTableRepository leagueTableRepository;
    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;

    @Transactional
    public void save(List<Long> memberIds, Long leagueId) {
        List<Member> members = memberRepository.findAllById(memberIds);
        Competition competition = competitionRepository.findById(leagueId).orElse(null);
        for (Member member : members) {
            LeagueTable leagueTable = LeagueTable.builder().member(member).competition(competition)
                    .win(0).draw(0).lose(0).goalFor(0).goalAgainst(0).build();
            leagueTableRepository.save(leagueTable);
        }
    }

    @Transactional
    public Long save(Long memberId, Long leagueId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        Competition competition = competitionRepository.findById(leagueId).orElse(null);
        if (member != null && competition != null) {
            LeagueTable leagueTable = LeagueTable.builder().member(member).competition(competition)
                    .win(0).draw(0).lose(0).goalFor(0).goalAgainst(0).build();
            return leagueTableRepository.save(leagueTable).getId();
        }
        return null;
    }

    @Transactional
    public void delete(Long id) {
        leagueTableRepository.deleteById(id);
    }

    @Transactional
    public void setNull(Long memberId) {
        List<LeagueTable> leagueTables = leagueTableRepository.findByMemberId(memberId);
        for (LeagueTable leagueTable : leagueTables) {
            leagueTable.updateMember(null);
        }
    }

    public List<LeagueTable> findByMemberId(Long memberId) {
        return leagueTableRepository.findByMemberId(memberId);
    }

    public List<LeagueTable> findByCompetitionId(Long competitionId) {
        return leagueTableRepository.findByCompetitionId(competitionId);
    }

    public LeagueTable findOne(Long memberId, Long competitionId) {
        return leagueTableRepository.findByMemberIdAndCompetitionId(memberId, competitionId).orElse(null);
    }

    public List<LeagueTable> findByCompetitionSort(Long competitionId) {
        return leagueTableRepository.findByCompetitionIdSort(competitionId);
    }
}
