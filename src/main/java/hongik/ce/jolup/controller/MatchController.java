package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.score.Score;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.MatchDto;
import hongik.ce.jolup.dto.ScoreDto;
import hongik.ce.jolup.service.JoinService;
import hongik.ce.jolup.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {

    private final MatchService matchService;
    private final JoinService joinService;

    @GetMapping("/{matchId}")
    public String matchDetail(@PathVariable("matchId") Long matchId, Model model) {
        MatchDto matchDto = matchService.getMatch(matchId);
        if (matchDto == null) {
            return "error";
        }
        ScoreDto scoreDto = matchDto.getScore().toDto();
        model.addAttribute("matchDto", matchDto);
        model.addAttribute("scoreDto", scoreDto);
//        model.addAttribute("matchStatuses", MatchStatus.values());
        return "match/update";
    }

    @PutMapping("/update/{matchId}")
    public String update(@PathVariable("matchId") Long matchId,
                         Score score, MatchStatus matchStatus) {
        MatchDto matchDto = matchService.getMatch(matchId);
        Long roomId = matchDto.getRoomDto().getId();
        JoinDto joinDto1 = joinService.findOne(matchDto.getUser1Dto(), matchDto.getRoomDto());
        Result result1 = joinDto1.getResult();
        JoinDto joinDto2 = joinService.findOne(matchDto.getUser2Dto(), matchDto.getRoomDto());
        Result result2 = joinDto2.getResult();

        if (matchDto.getMatchStatus().equals(MatchStatus.END)) {
            if (matchDto.getScore().getUser1Score() > matchDto.getScore().getUser2Score()) {
                result1.setWin(result1.getWin() - 1);
                result2.setLose(result2.getLose() - 1);
            } else if (matchDto.getScore().getUser1Score() < matchDto.getScore().getUser2Score()) {
                result1.setLose(result1.getLose() - 1);
                result2.setWin(result2.getWin() - 1);
            } else {
                result1.setDraw(result1.getDraw() - 1);
                result2.setDraw(result2.getDraw() - 1);
            }
            result1.setPlays(result1.getPlays() - 1);
            result1.setGoalFor(result1.getGoalFor() - matchDto.getScore().getUser1Score());
            result1.setGoalAgainst(result1.getGoalAgainst() - matchDto.getScore().getUser2Score());
            result1.setGoalDifference(result1.getGoalFor() - result1.getGoalAgainst());
            result1.setPoints(result1.getWin() * 3 + result1.getDraw());

            result2.setPlays(result2.getPlays() - 1);
            result2.setGoalFor(result2.getGoalFor() - matchDto.getScore().getUser2Score());
            result2.setGoalAgainst(result2.getGoalAgainst() - matchDto.getScore().getUser1Score());
            result2.setGoalDifference(result2.getGoalFor() - result2.getGoalAgainst());
            result2.setPoints(result2.getWin() * 3 + result2.getDraw());
        }

        if (matchStatus == MatchStatus.READY) {
            matchDto.setScore(Score.builder().user1Score(0).user2Score(0).build());
            matchDto.setMatchStatus(matchStatus);
        } else {
            if (score.getUser1Score() > score.getUser2Score()) {
                result1.setWin(result1.getWin() + 1);
                result2.setLose(result2.getLose() + 1);
            } else if (score.getUser1Score() < score.getUser2Score()) {
                result1.setLose(result1.getLose() + 1);
                result2.setWin(result2.getWin() + 1);
            } else {
                result1.setDraw(result1.getDraw() + 1);
                result2.setDraw(result2.getDraw() + 1);
            }
            result1.setPlays(result1.getPlays() + 1);
            result1.setGoalFor(result1.getGoalFor() + score.getUser1Score());
            result1.setGoalAgainst(result1.getGoalAgainst() + score.getUser2Score());
            result1.setGoalDifference(result1.getGoalFor() - result1.getGoalAgainst());
            result1.setPoints(result1.getWin() * 3 + result1.getDraw());

            result2.setPlays(result2.getPlays() + 1);
            result2.setGoalFor(result2.getGoalFor() + score.getUser2Score());
            result2.setGoalAgainst(result2.getGoalAgainst() + score.getUser1Score());
            result2.setGoalDifference(result2.getGoalFor() - result2.getGoalAgainst());
            result2.setPoints(result2.getWin() * 3 + result2.getDraw());
            matchDto.setScore(score);
            matchDto.setMatchStatus(matchStatus);
        }
        matchService.saveMatch(matchDto);
        joinDto1.setResult(result1);
        joinDto2.setResult(result2);
        joinService.saveJoin(joinDto1);
        joinService.saveJoin(joinDto2);
        return "redirect:/room/" + roomId;
    }
}
