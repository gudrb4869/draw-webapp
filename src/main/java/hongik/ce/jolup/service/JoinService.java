package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.score.Score;
import hongik.ce.jolup.repository.CompetitionRepository;
import hongik.ce.jolup.repository.JoinRepository;
import hongik.ce.jolup.repository.MatchRepository;
import hongik.ce.jolup.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoinService {

    private final JoinRepository joinRepository;
    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;
    private final MatchRepository matchRepository;

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
    public Long save(Long memberId, Long competitionId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        Competition competition = competitionRepository.findById(competitionId).orElse(null);
        if (member != null && competition != null) {
            Result result = Result.builder().win(0).draw(0).lose(0).goalFor(0).goalAgainst(0).build();
            Join join = Join.builder().member(member).competition(competition).result(result).build();
            return joinRepository.save(join).getId();
        }
        return null;
    }

    @Transactional
    public void update(Long homeId, Long awayId, Long competitionId, Long matchId, Integer homeScore, Integer awayScore, MatchStatus matchStatus) {
        Join homeJoin = joinRepository.findByMemberIdAndCompetitionId(homeId, competitionId).orElse(null);
        Join awayJoin = joinRepository.findByMemberIdAndCompetitionId(awayId, competitionId).orElse(null);
        Competition competition = competitionRepository.findById(competitionId).orElse(null);
        Match match = matchRepository.findById(matchId).orElse(null);
        if (homeJoin == null || awayJoin == null || competition == null || match == null) {
            return;
        }

        Result homeResult = homeJoin.getResult();
        Result awayResult = awayJoin.getResult();
        if (homeResult == null || awayResult == null) {
            return;
        }

        if (match.getMatchStatus().equals(MatchStatus.END)) {
            if (match.getScore().getHomeScore() > match.getScore().getAwayScore()) {
                homeResult.subWin(1);
                awayResult.subLose(1);
                if (competition.getType().equals(CompetitionType.TOURNAMENT) && (homeScore <= awayScore)) {
                    resetTournamentMatches(competitionId, match);
                }
            } else if (match.getScore().getHomeScore() < match.getScore().getAwayScore()) {
                if (competition.getType().equals(CompetitionType.TOURNAMENT) && (homeScore >= awayScore)) {
                    resetTournamentMatches(competitionId, match);
                }
                homeResult.subLose(1);
                awayResult.subWin(1);
            } else {
                homeResult.subDraw(1);
                awayResult.subDraw(1);
            }
            homeResult.subGoalFor(match.getScore().getHomeScore());
            homeResult.subGoalAgainst(match.getScore().getAwayScore());
            awayResult.subGoalFor(match.getScore().getAwayScore());
            awayResult.subGoalAgainst(match.getScore().getHomeScore());
        }

        if (matchStatus.equals(MatchStatus.END)) {
            if (homeScore > awayScore) {
                homeResult.addWin(1);
                awayResult.addLose(1);
                if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
                    setTournamentMatch(competitionId, match, match.getHome());
                }
            } else if (homeScore < awayScore) {
                homeResult.addLose(1);
                awayResult.addWin(1);
                if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
                    setTournamentMatch(competitionId, match, match.getAway());
                }
            } else {
                homeResult.addDraw(1);
                awayResult.addDraw(1);
            }
            homeResult.addGoalFor(homeScore);
            homeResult.addGoalAgainst(awayScore);
            awayResult.addGoalFor(awayScore);
            awayResult.addGoalAgainst(homeScore);
        }

        homeJoin.updateResult(homeResult);
        awayJoin.updateResult(awayResult);
        Score score = Score.builder().homeScore(homeScore).awayScore(awayScore).build();
        match.update(score, matchStatus);
    }

    private void setTournamentMatch(Long competitionId, Match match, Member member) {
        Match nextMatch = matchRepository
                .findByCompetitionIdAndRoundNoAndMatchNo(competitionId, match.getRoundNo() - 1, match.getMatchNo() / 2)
                .orElse(null);
        if (nextMatch != null) {
            if (match.getMatchNo() % 2 == 0) {
                nextMatch.updateHome(member);
            } else {
                nextMatch.updateAway(member);
            }
        }
    }

    private void resetTournamentMatches(Long competitionId, Match match) {
        Match cur = match;
        Match next = matchRepository
                .findByCompetitionIdAndRoundNoAndMatchNo(competitionId, cur.getRoundNo() - 1, cur.getMatchNo() / 2)
                .orElse(null);
        while (next != null) {
            if (cur.getMatchNo() % 2 == 0) {
                next.updateHome(null);
            } else {
                next.updateAway(null);
            }
            Score score = Score.builder().homeScore(0).awayScore(0).build();
            next.updateScore(score);
            next.updateMatchStatus(MatchStatus.READY);
            cur = next;
            next = matchRepository
                    .findByCompetitionIdAndRoundNoAndMatchNo(competitionId, cur.getRoundNo() - 1, cur.getMatchNo() / 2)
                    .orElse(null);
        }
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
