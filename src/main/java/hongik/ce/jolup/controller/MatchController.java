package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.score.Score;
import hongik.ce.jolup.service.BelongService;
import hongik.ce.jolup.service.CompetitionService;
import hongik.ce.jolup.service.JoinService;
import hongik.ce.jolup.service.MatchService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

        log.info("GET updateMatch : roomId = {}, competitionId = {}, matchId = {}", roomId, competitionId, matchId);
        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        Match match = matchService.findByIdAndCompetitionId(matchId, competitionId);
        if (match == null) {
            return "error";
        }

        MatchUpdateForm matchUpdateForm = MatchUpdateForm.builder().id(match.getId())
                .homeScore(match.getScore().getHomeScore()).awayScore(match.getScore().getAwayScore()).build();
        if (match.getHome() != null) {
            matchUpdateForm.setHomeId(match.getHome().getId());
            matchUpdateForm.setHome(match.getHome().getEmail() + "(" + match.getHome().getName() + ")");
        }
        if (match.getAway() != null) {
            matchUpdateForm.setAwayId(match.getAway().getId());
            matchUpdateForm.setAway(match.getAway().getEmail() + "(" + match.getAway().getName() + ")");
        }
        log.info("matchUpdateForm = {}", matchUpdateForm);
        model.addAttribute("form", matchUpdateForm);
        return "match/update";
    }

    @PostMapping("/{matchId}/update")
    public String update(@PathVariable("roomId") Long roomId,
                         @PathVariable("competitionId") Long competitionId,
                         @PathVariable("matchId") Long matchId, @AuthenticationPrincipal Member member,
                         @ModelAttribute("form") @Valid MatchUpdateForm matchUpdateForm,
                         BindingResult bindingResult) {

        log.info("POST updateMatch : roomId = {}, competitionId = {}, matchId = {}", roomId, competitionId, matchId);
        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }
        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        Match match = matchService.findByIdAndCompetitionId(matchId, competitionId);
        if (match == null) {
            return "error";
        }

        if (bindingResult.hasErrors()) {
            return "match/update";
        }

        /*if (matchStatus.equals(MatchStatus.READY)) {
            score.setHomeScore(0);
            score.setAwayScore(0);
        }*/

        Join homeJoin = joinService.findOne(matchUpdateForm.getHomeId(), competitionId);
        Result homeResult = homeJoin.getResult();
        Join awayJoin = joinService.findOne(matchUpdateForm.getAwayId(), competitionId);
        Result awayResult = awayJoin.getResult();

        /*if (match.getMatchStatus().equals(MatchStatus.END)) {
            if (match.getScore().getHomeScore() > match.getScore().getAwayScore()) {
                homeResult.setWin(homeResult.getWin() - 1);
                awayResult.setLose(awayResult.getLose() - 1);

                if (competition.getCompetitionType().equals(CompetitionType.TOURNAMENT) && !(score.getHomeScore() > score.getAwayScore())) {
                    resetTournamentMatch(match, competitionId);
                }

            } else if (match.getScore().getHomeScore() < match.getScore().getAwayScore()) {
                homeResult.setLose(homeResult.getLose() - 1);
                awayResult.setWin(awayResult.getWin() - 1);
                if (competition.getCompetitionType().equals(CompetitionType.TOURNAMENT) && !(score.getHomeScore() < score.getAwayScore())) {
                    resetTournamentMatch(match, competitionId);
                }
            } else {
                homeResult.setDraw(homeResult.getDraw() - 1);
                awayResult.setDraw(awayResult.getDraw() - 1);
            }
            homeResult.setPlays(homeResult.getPlays() - 1);
            homeResult.setGoalFor(homeResult.getGoalFor() - match.getScore().getHomeScore());
            homeResult.setGoalAgainst(homeResult.getGoalAgainst() - match.getScore().getAwayScore());
            homeResult.setGoalDifference(homeResult.getGoalFor() - homeResult.getGoalAgainst());
            homeResult.setPoints(homeResult.getWin() * 3 + homeResult.getDraw());

            awayResult.setPlays(awayResult.getPlays() - 1);
            awayResult.setGoalFor(awayResult.getGoalFor() - match.getScore().getAwayScore());
            awayResult.setGoalAgainst(awayResult.getGoalAgainst() - match.getScore().getHomeScore());
            awayResult.setGoalDifference(awayResult.getGoalFor() - awayResult.getGoalAgainst());
            awayResult.setPoints(awayResult.getWin() * 3 + awayResult.getDraw());
        }

        if (matchStatus.equals(MatchStatus.END)) {
            if (score.getHomeScore() > score.getAwayScore()) {
                homeResult.setWin(homeResult.getWin() + 1);
                awayResult.setLose(awayResult.getLose() + 1);
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
                homeResult.setLose(homeResult.getLose() + 1);
                awayResult.setWin(awayResult.getWin() + 1);
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
                homeResult.setDraw(homeResult.getDraw() + 1);
                awayResult.setDraw(awayResult.getDraw() + 1);
            }
            homeResult.setPlays(homeResult.getPlays() + 1);
            homeResult.setGoalFor(homeResult.getGoalFor() + score.getHomeScore());
            homeResult.setGoalAgainst(homeResult.getGoalAgainst() + score.getAwayScore());
            homeResult.setGoalDifference(homeResult.getGoalFor() - homeResult.getGoalAgainst());
            homeResult.setPoints(homeResult.getWin() * 3 + homeResult.getDraw());

            awayResult.setPlays(awayResult.getPlays() + 1);
            awayResult.setGoalFor(awayResult.getGoalFor() + score.getAwayScore());
            awayResult.setGoalAgainst(awayResult.getGoalAgainst() + score.getHomeScore());
            awayResult.setGoalDifference(awayResult.getGoalFor() - awayResult.getGoalAgainst());
            awayResult.setPoints(awayResult.getWin() * 3 + awayResult.getDraw());
        }
        match.updateScore(score);
        match.updateMatchStatus(matchStatus);
        matchService.saveMatch(match);
        homeJoin.updateResult(homeResult);
        awayJoin.updateResult(awayResult);
        joinService.saveJoin(homeJoin);
        joinService.saveJoin(awayJoin);*/
        return "redirect:/rooms/{roomId}/competitions/{competitionId}";
    }

    @ToString @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    private static class MatchUpdateForm {
        @NotNull
        private Long id;

        @NotNull
        private Long homeId;
        private String home;
        private String away;
        @NotNull
        private Long awayId;

        @NotNull
        private Integer homeScore;
        @NotNull
        private Integer awayScore;
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
