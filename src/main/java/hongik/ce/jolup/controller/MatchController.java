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
        if (match == null || match.getMatchStatus().equals(MatchStatus.END)) {
            return "error";
        }

        MatchUpdateForm form = MatchUpdateForm.builder().id(match.getId())
                .homeScore(match.getScore().getHomeScore()).awayScore(match.getScore().getAwayScore()).build();
        if (match.getHome() != null) {
            form.setHomeId(match.getHome().getId());
            form.setHome(match.getHome().getEmail() + "(" + match.getHome().getName() + ")");
        }
        if (match.getAway() != null) {
            form.setAwayId(match.getAway().getId());
            form.setAway(match.getAway().getEmail() + "(" + match.getAway().getName() + ")");
        }
        log.info("matchUpdateForm = {}", form);
        model.addAttribute("form", form);
        return "match/update";
    }

    @PostMapping("/{matchId}/update")
    public String update(@PathVariable("roomId") Long roomId,
                         @PathVariable("competitionId") Long competitionId,
                         @PathVariable("matchId") Long matchId, @AuthenticationPrincipal Member member,
                         @ModelAttribute("form") @Valid MatchUpdateForm form,
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
        if (match == null || match.getMatchStatus().equals(MatchStatus.END)) {
            return "error";
        }

        if (bindingResult.hasErrors()) {
            return "match/update";
        }

        log.info("matchUpdateForm = {}", form);

        Join homeJoin = joinService.findOne(form.getHomeId(), competitionId);
        Result homeResult = homeJoin.getResult();
        Join awayJoin = joinService.findOne(form.getAwayId(), competitionId);
        Result awayResult = awayJoin.getResult();

        if (match.getMatchStatus().equals(MatchStatus.END)) {
            if (match.getScore().getHomeScore() > match.getScore().getAwayScore()) {
                homeResult.setWin(homeResult.getWin() - 1);
                awayResult.setLose(awayResult.getLose() - 1);

                if (competition.getCompetitionType().equals(CompetitionType.TOURNAMENT) && !(form.getHomeScore() > form.getAwayScore())) {
                    Match curMatch = match;
                    Match nextMatch = matchService.findOne(competitionId, curMatch.getRoundNo() - 1, curMatch.getMatchNo() / 2);
                    while (nextMatch != null) {
                        if (curMatch.getMatchNo() % 2 == 0) {
                            nextMatch.updateHome(null);
                        } else {
                            nextMatch.updateAway(null);
                        }
                        Score score = Score.builder().homeScore(0).awayScore(0).build();
                        nextMatch.updateScore(score);
                        nextMatch.updateMatchStatus(MatchStatus.READY);
                        curMatch = nextMatch;
                        nextMatch = matchService.findOne(competitionId, curMatch.getRoundNo() - 1, curMatch.getMatchNo() / 2);
                    }
                }

            } else if (match.getScore().getHomeScore() < match.getScore().getAwayScore()) {
                homeResult.setLose(homeResult.getLose() - 1);
                awayResult.setWin(awayResult.getWin() - 1);
                if (competition.getCompetitionType().equals(CompetitionType.TOURNAMENT) && !(form.getHomeScore() < form.getAwayScore())) {
                    Match curMatch = match;
                    Match nextMatch = matchService.findOne(competitionId, curMatch.getRoundNo() - 1, curMatch.getMatchNo() / 2);
                    while (nextMatch != null) {
                        if (curMatch.getMatchNo() % 2 == 0) {
                            nextMatch.updateHome(null);
                        } else {
                            nextMatch.updateAway(null);
                        }
                        Score score = Score.builder().homeScore(0).awayScore(0).build();
                        nextMatch.updateScore(score);
                        nextMatch.updateMatchStatus(MatchStatus.READY);
                        curMatch = nextMatch;
                        nextMatch = matchService.findOne(competitionId, curMatch.getRoundNo() - 1, curMatch.getMatchNo() / 2);
                    }
                }
            } else {
                homeResult.setDraw(homeResult.getDraw() - 1);
                awayResult.setDraw(awayResult.getDraw() - 1);
            }
            homeResult.setGoalFor(homeResult.getGoalFor() - match.getScore().getHomeScore());
            homeResult.setGoalAgainst(homeResult.getGoalAgainst() - match.getScore().getAwayScore());

            awayResult.setGoalFor(awayResult.getGoalFor() - match.getScore().getAwayScore());
            awayResult.setGoalAgainst(awayResult.getGoalAgainst() - match.getScore().getHomeScore());
        }

        if (form.getHomeScore() > form.getAwayScore()) {
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
                }
            }
        } else if (form.getHomeScore() < form.getAwayScore()) {
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
                }
            }
        } else {
            homeResult.setDraw(homeResult.getDraw() + 1);
            awayResult.setDraw(awayResult.getDraw() + 1);
        }
        homeResult.setGoalFor(homeResult.getGoalFor() + form.getHomeScore());
        homeResult.setGoalAgainst(homeResult.getGoalAgainst() + form.getAwayScore());

        awayResult.setGoalFor(awayResult.getGoalFor() + form.getAwayScore());
        awayResult.setGoalAgainst(awayResult.getGoalAgainst() + form.getHomeScore());
        homeJoin.updateResult(homeResult);
        awayJoin.updateResult(awayResult);
        matchService.update(form.getId(), MatchStatus.END, form.getHomeScore(), form.getAwayScore());
        return "redirect:/rooms/{roomId}/competitions/{competitionId}";
    }

    @PostMapping("/{matchId}/init")
    public String init(@PathVariable("roomId") Long roomId,
                       @PathVariable("competitionId") Long competitionId,
                       @PathVariable("matchId") Long matchId,
                       @AuthenticationPrincipal Member member) {

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

        matchService.update(matchId, MatchStatus.READY, 0, 0);
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
}
