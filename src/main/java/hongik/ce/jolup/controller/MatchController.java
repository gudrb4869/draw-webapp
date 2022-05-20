package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.BelongType;
//import hongik.ce.jolup.domain.join.JoinRole;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.domain.score.Score;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.dto.*;
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
        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null || !myBelongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        CompetitionDto competitionDto = competitionService.findOne(competitionId, roomId);
        if (competitionDto == null) {
            return "error";
        }

        MatchDto matchDto = matchService.findByIdAndCompetitionId(matchId, competitionId);
        if (matchDto == null || matchDto.getHomeDto() == null || matchDto.getAwayDto() == null) {
            return "error";
        }

        ScoreDto scoreDto = matchDto.getScore().toDto();
        model.addAttribute("matchDto", matchDto);
        model.addAttribute("scoreDto", scoreDto);
        return "match/update";
    }

    @PutMapping("/{matchId}/update")
    public String update(@PathVariable("roomId") Long roomId,
                         @PathVariable("competitionId") Long competitionId,
                         @PathVariable("matchId") Long matchId, @AuthenticationPrincipal Member member,
                         Score score, MatchStatus matchStatus) {

        log.info("PUT : updateMatch = {}", roomId, competitionId, matchId);
        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null || !myBelongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }
        CompetitionDto competitionDto = competitionService.findOne(competitionId, roomId);
        if (competitionDto == null) {
            return "error";
        }

        MatchDto matchDto = matchService.findByIdAndCompetitionId(matchId, competitionId);
        if (matchDto == null || matchDto.getHomeDto() == null || matchDto.getAwayDto() == null) {
            return "error";
        }

        if (matchStatus.equals(MatchStatus.READY)) {
            score.setHomeScore(0);
            score.setAwayScore(0);
        }

        JoinDto joinDto1 = joinService.findOne(extractBelongDto(roomId, matchDto.getHomeDto().getId()), competitionId);
        Result result1 = joinDto1.getResult();
        JoinDto joinDto2 = joinService.findOne(extractBelongDto(roomId, matchDto.getAwayDto().getId()), competitionId);
        Result result2 = joinDto2.getResult();

        if (matchDto.getMatchStatus().equals(MatchStatus.END)) {
            if (matchDto.getScore().getHomeScore() > matchDto.getScore().getAwayScore()) {
                result1.setWin(result1.getWin() - 1);
                result2.setLose(result2.getLose() - 1);

                if (competitionDto.getCompetitionType().equals(CompetitionType.TOURNAMENT) && !(score.getHomeScore() > score.getAwayScore())) {
                    resetTournamentMatch(matchDto, competitionDto);
                }

            } else if (matchDto.getScore().getHomeScore() < matchDto.getScore().getAwayScore()) {
                result1.setLose(result1.getLose() - 1);
                result2.setWin(result2.getWin() - 1);
                if (competitionDto.getCompetitionType().equals(CompetitionType.TOURNAMENT) && !(score.getHomeScore() < score.getAwayScore())) {
                    resetTournamentMatch(matchDto, competitionDto);
                }
            } else {
                result1.setDraw(result1.getDraw() - 1);
                result2.setDraw(result2.getDraw() - 1);
            }
            result1.setPlays(result1.getPlays() - 1);
            result1.setGoalFor(result1.getGoalFor() - matchDto.getScore().getHomeScore());
            result1.setGoalAgainst(result1.getGoalAgainst() - matchDto.getScore().getAwayScore());
            result1.setGoalDifference(result1.getGoalFor() - result1.getGoalAgainst());
            result1.setPoints(result1.getWin() * 3 + result1.getDraw());

            result2.setPlays(result2.getPlays() - 1);
            result2.setGoalFor(result2.getGoalFor() - matchDto.getScore().getAwayScore());
            result2.setGoalAgainst(result2.getGoalAgainst() - matchDto.getScore().getHomeScore());
            result2.setGoalDifference(result2.getGoalFor() - result2.getGoalAgainst());
            result2.setPoints(result2.getWin() * 3 + result2.getDraw());
        }

        if (matchStatus.equals(MatchStatus.END)) {
            if (score.getHomeScore() > score.getAwayScore()) {
                result1.setWin(result1.getWin() + 1);
                result2.setLose(result2.getLose() + 1);
                if (competitionDto.getCompetitionType().equals(CompetitionType.TOURNAMENT)) {
                    MatchDto nextMatchDto = matchService.findOne(competitionDto, matchDto.getRoundNo() - 1, matchDto.getMatchNo() / 2);
                    if (nextMatchDto != null) {
                        if (matchDto.getMatchNo() % 2 == 0) {
                            nextMatchDto.setHomeDto(matchDto.getHomeDto());
                        } else {
                            nextMatchDto.setAwayDto(matchDto.getHomeDto());
                        }
                        matchService.saveMatch(nextMatchDto);
                    }
                }
            } else if (score.getHomeScore() < score.getAwayScore()) {
                result1.setLose(result1.getLose() + 1);
                result2.setWin(result2.getWin() + 1);
                if (competitionDto.getCompetitionType().equals(CompetitionType.TOURNAMENT)) {
                    MatchDto nextMatchDto = matchService.findOne(competitionDto, matchDto.getRoundNo() - 1, matchDto.getMatchNo() / 2);
                    if (nextMatchDto != null) {
                        if (matchDto.getMatchNo() % 2 == 0) {
                            nextMatchDto.setHomeDto(matchDto.getAwayDto());
                        } else {
                            nextMatchDto.setAwayDto(matchDto.getAwayDto());
                        }
                        matchService.saveMatch(nextMatchDto);
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
        matchDto.setScore(score);
        matchDto.setMatchStatus(matchStatus);
        matchService.saveMatch(matchDto);
        joinDto1.setResult(result1);
        joinDto2.setResult(result2);
        joinService.saveJoin(joinDto1);
        joinService.saveJoin(joinDto2);
        return "redirect:/rooms/{roomId}/competitions/{competitionId}";
    }

    private Long extractBelongDto(Long roomId, Long memberId) {
        BelongDto belongDto = belongService.findOne(memberId, roomId);
        if (belongDto == null)
            return null;
        return belongDto.getId();
    }

    private void resetTournamentMatch(MatchDto matchDto, CompetitionDto competitionDto) {
        MatchDto curMatchDto = matchDto;
        MatchDto nextMatchDto = matchService.findOne(competitionDto, curMatchDto.getRoundNo() - 1, curMatchDto.getMatchNo() / 2);
        while (nextMatchDto != null) {
            if (curMatchDto.getMatchNo() % 2 == 0) {
                nextMatchDto.setHomeDto(null);
            } else {
                nextMatchDto.setAwayDto(null);
            }
            nextMatchDto.setScore(Score.builder().homeScore(0).awayScore(0).build());
            nextMatchDto.setMatchStatus(MatchStatus.READY);
            matchService.saveMatch(nextMatchDto);
            curMatchDto = nextMatchDto;
            nextMatchDto = matchService.findOne(competitionDto, curMatchDto.getRoundNo() - 1, curMatchDto.getMatchNo() / 2);
        }
    }
}
