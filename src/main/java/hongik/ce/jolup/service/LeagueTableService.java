package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.LeagueGame;
import hongik.ce.jolup.domain.competition.LeagueTable;
import hongik.ce.jolup.domain.competition.Status;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.repository.*;
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
    private final LeagueGameRepository leagueGameRepository;

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
    public void update(Long homeId, Long awayId, Long leagueId, Long leagueGameId, Integer homeScore, Integer awayScore, Status status) {
        LeagueTable homeLeagueTable = leagueTableRepository.findByMemberIdAndCompetitionId(homeId, leagueId).orElse(null);
        LeagueTable awayLeagueTable = leagueTableRepository.findByMemberIdAndCompetitionId(awayId, leagueId).orElse(null);
        Competition competition = competitionRepository.findById(leagueId).orElse(null);
        LeagueGame leagueGame = leagueGameRepository.findById(leagueGameId).orElse(null);
        if (homeLeagueTable == null || awayLeagueTable == null || competition == null || leagueGame == null) {
            return;
        }

        if (leagueGame.getStatus().equals(Status.AFTER)) {
            if (leagueGame.getHomeScore() > leagueGame.getAwayScore()) {
                homeLeagueTable.subWin(1);
                awayLeagueTable.subLose(1);
            } else if (leagueGame.getHomeScore() < leagueGame.getAwayScore()) {
                homeLeagueTable.subLose(1);
                awayLeagueTable.subWin(1);
            } else {
                homeLeagueTable.subDraw(1);
                awayLeagueTable.subDraw(1);
            }
            homeLeagueTable.subGoalFor(leagueGame.getHomeScore());
            homeLeagueTable.subGoalAgainst(leagueGame.getAwayScore());
            awayLeagueTable.subGoalFor(leagueGame.getAwayScore());
            awayLeagueTable.subGoalAgainst(leagueGame.getHomeScore());
        }

        if (status.equals(Status.AFTER)) {
            if (homeScore > awayScore) {
                homeLeagueTable.addWin(1);
                awayLeagueTable.addLose(1);
            } else if (homeScore < awayScore) {
                homeLeagueTable.addLose(1);
                awayLeagueTable.addWin(1);
            } else {
                homeLeagueTable.addDraw(1);
                awayLeagueTable.addDraw(1);
            }
            homeLeagueTable.addGoalFor(homeScore);
            homeLeagueTable.addGoalAgainst(awayScore);
            awayLeagueTable.addGoalFor(awayScore);
            awayLeagueTable.addGoalAgainst(homeScore);
        }

        leagueGame.updateStatus(status);
        leagueGame.updateScore(homeScore, awayScore);
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
