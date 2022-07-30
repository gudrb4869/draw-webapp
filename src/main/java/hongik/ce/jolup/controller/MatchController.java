package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.domain.score.Score;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.service.BelongService;
import hongik.ce.jolup.service.CompetitionService;
import hongik.ce.jolup.service.JoinService;
import hongik.ce.jolup.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms/{roomId}/competitions/{competitionId}/matches")
public class MatchController {

    private final MatchService matchService;
    private final JoinService joinService;
    private final BelongService belongService;
    private final CompetitionService competitionService;

    @GetMapping("/{matchId}/update")
    public String update(@PathVariable("roomId") Long roomId,
                         @PathVariable("competitionId") Long competitionId,
                         @PathVariable("matchId") Long matchId,
                         @AuthenticationPrincipal Member member,
                         Model model) {

        log.info("GET : updateMatch = {}", roomId, competitionId, matchId);
        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        Match match = matchService.findByIdAndCompetitionId(matchId, competitionId);
        if (match == null || match.getHome() == null || match.getAway() == null) {
            return "error";
        }

        model.addAttribute("match", match);
        return "match/update";
    }

    @PostMapping("/{matchId}/update")
    public String update(@PathVariable("roomId") Long roomId,
                         @PathVariable("competitionId") Long competitionId,
                         @PathVariable("matchId") Long matchId, @AuthenticationPrincipal Member member,
                         Score score, MatchStatus matchStatus) {

        log.info("POST : updateMatch = {}", roomId, competitionId, matchId);
        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }
        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        Match match = matchService.findByIdAndCompetitionId(matchId, competitionId);
        if (match == null || match.getHome() == null || match.getAway() == null) {
            return "error";
        }

        if (matchStatus.equals(MatchStatus.READY)) {
            score.setHomeScore(0);
            score.setAwayScore(0);
        }

        Join join1 = joinService.findOne(extractBelong(match.getHome().getId(), roomId), competitionId);
        Result result1 = join1.getResult();
        Join join2 = joinService.findOne(extractBelong(match.getAway().getId(), roomId), competitionId);
        Result result2 = join2.getResult();

        if (match.getMatchStatus().equals(MatchStatus.END)) {
            if (match.getScore().getHomeScore() > match.getScore().getAwayScore()) {
                result1.setWin(result1.getWin() - 1);
                result2.setLose(result2.getLose() - 1);

                if (competition.getCompetitionType().equals(CompetitionType.TOURNAMENT) && !(score.getHomeScore() > score.getAwayScore())) {
                    resetTournamentMatch(match, competitionId);
                }

            } else if (match.getScore().getHomeScore() < match.getScore().getAwayScore()) {
                result1.setLose(result1.getLose() - 1);
                result2.setWin(result2.getWin() - 1);
                if (competition.getCompetitionType().equals(CompetitionType.TOURNAMENT) && !(score.getHomeScore() < score.getAwayScore())) {
                    resetTournamentMatch(match, competitionId);
                }
            } else {
                result1.setDraw(result1.getDraw() - 1);
                result2.setDraw(result2.getDraw() - 1);
            }
            result1.setPlays(result1.getPlays() - 1);
            result1.setGoalFor(result1.getGoalFor() - match.getScore().getHomeScore());
            result1.setGoalAgainst(result1.getGoalAgainst() - match.getScore().getAwayScore());
            result1.setGoalDifference(result1.getGoalFor() - result1.getGoalAgainst());
            result1.setPoints(result1.getWin() * 3 + result1.getDraw());

            result2.setPlays(result2.getPlays() - 1);
            result2.setGoalFor(result2.getGoalFor() - match.getScore().getAwayScore());
            result2.setGoalAgainst(result2.getGoalAgainst() - match.getScore().getHomeScore());
            result2.setGoalDifference(result2.getGoalFor() - result2.getGoalAgainst());
            result2.setPoints(result2.getWin() * 3 + result2.getDraw());
        }

        if (matchStatus.equals(MatchStatus.END)) {
            if (score.getHomeScore() > score.getAwayScore()) {
                result1.setWin(result1.getWin() + 1);
                result2.setLose(result2.getLose() + 1);
                if (competition.getCompetitionType().equals(CompetitionType.TOURNAMENT)) {
                    Match nextMatch = matchService.findOne(competitionId, match.getRoundNo() - 1, match.getMatchNo() / 2);
                    if (nextMatch != null) {
                        if (match.getMatchNo() % 2 == 0) {
                            nextMatch.updateHome(match.getHome());
                        } else {
                            nextMatch.updateAway(match.getHome());
                        }
                        matchService.saveMatch(nextMatch);
                    }
                }
            } else if (score.getHomeScore() < score.getAwayScore()) {
                result1.setLose(result1.getLose() + 1);
                result2.setWin(result2.getWin() + 1);
                if (competition.getCompetitionType().equals(CompetitionType.TOURNAMENT)) {
                    Match nextMatch = matchService.findOne(competitionId, match.getRoundNo() - 1, match.getMatchNo() / 2);
                    if (nextMatch != null) {
                        if (match.getMatchNo() % 2 == 0) {
                            nextMatch.updateHome(match.getAway());
                        } else {
                            nextMatch.updateAway(match.getAway());
                        }
                        matchService.saveMatch(nextMatch);
                    }
                }
            } else {
                result1.setDraw(result1.getDraw() + 1);
                result2.setDraw(result2.getDraw() + 1);
            }
            result1.setPlays(result1.getPlays() + 1);
            result1.setGoalFor(result1.getGoalFor() + score.getHomeScore());
            result1.setGoalAgainst(result1.getGoalAgainst() + score.getAwayScore());
            result1.setGoalDifference(result1.getGoalFor() - result1.getGoalAgainst());
            result1.setPoints(result1.getWin() * 3 + result1.getDraw());

            result2.setPlays(result2.getPlays() + 1);
            result2.setGoalFor(result2.getGoalFor() + score.getAwayScore());
            result2.setGoalAgainst(result2.getGoalAgainst() + score.getHomeScore());
            result2.setGoalDifference(result2.getGoalFor() - result2.getGoalAgainst());
            result2.setPoints(result2.getWin() * 3 + result2.getDraw());
        }
        match.updateScore(score);
        match.updateMatchStatus(matchStatus);
        matchService.saveMatch(match);
        join1.updateResult(result1);
        join2.updateResult(result2);
        joinService.saveJoin(join1);
        joinService.saveJoin(join2);
        return "redirect:/rooms/{roomId}/competitions/{competitionId}";
    }

    private Long extractBelong(Long memberId, Long roomId) {
        Belong belong = belongService.findOne(memberId, roomId);
        if (belong == null)
            return null;
        return belong.getId();
    }

    private void resetTournamentMatch(Match match, Long competitionId) {
        Match curMatch = match;
        Match nextMatch = matchService.findOne(competitionId, curMatch.getRoundNo() - 1, curMatch.getMatchNo() / 2);
        while (nextMatch != null) {
            if (curMatch.getMatchNo() % 2 == 0) {
                nextMatch.updateHome(null);
            } else {
                nextMatch.updateAway(null);
            }
            nextMatch.updateScore(Score.builder().homeScore(0).awayScore(0).build());
            nextMatch.updateMatchStatus(MatchStatus.READY);
            matchService.saveMatch(nextMatch);
            curMatch = nextMatch;
            nextMatch = matchService.findOne(competitionId, curMatch.getRoundNo() - 1, curMatch.getMatchNo() / 2);
        }
    }
}
