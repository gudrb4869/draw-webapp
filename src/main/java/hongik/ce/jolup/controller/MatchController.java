package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.match.Match;
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

    @GetMapping("/{no}")
    public String matchDetail(@PathVariable("no") Long no, Model model) {
        MatchDto matchDto = matchService.findById(no);
        ScoreDto scoreDto = Score.toDto(matchDto.getScore());
        model.addAttribute("matchDto", matchDto);
        model.addAttribute("scoreDto", scoreDto);
        model.addAttribute("matchStatuses", MatchStatus.values());
        return "match/update";
    }

    @PutMapping("/update/{no}")
    public String update(@PathVariable("no") Long no,
                         Score score, MatchStatus matchStatus) {
        Match match = matchService.findById(no).toEntity();
        Long roomId = match.getRoom().getId();
        Join join1 = joinService.findOne(match.getUser1(), match.getRoom());
        Result result1 = join1.getResult();
        Join join2 = joinService.findOne(match.getUser2(), match.getRoom());
        Result result2 = join2.getResult();

        if (match.getMatchStatus().equals(MatchStatus.END)) {
            if (match.getScore().getUser1Score() > match.getScore().getUser2Score()) {
                result1.setWin(result1.getWin() - 1);
                result2.setLose(result2.getLose() - 1);
            } else if (match.getScore().getUser1Score() < match.getScore().getUser2Score()) {
                result1.setLose(result1.getLose() - 1);
                result2.setWin(result2.getWin() - 1);
            } else {
                result1.setDraw(result1.getDraw() - 1);
                result2.setDraw(result2.getDraw() - 1);
            }
            result1.setPlays(result1.getPlays() - 1);
            result1.setGoalFor(result1.getGoalFor() - match.getScore().getUser1Score());
            result1.setGoalAgainst(result1.getGoalAgainst() - match.getScore().getUser2Score());
            result1.setGoalDifference(result1.getGoalFor() - result1.getGoalAgainst());
            result1.setPoints(result1.getWin() * 3 + result1.getDraw());

            result2.setPlays(result2.getPlays() - 1);
            result2.setGoalFor(result2.getGoalFor() - match.getScore().getUser2Score());
            result2.setGoalAgainst(result2.getGoalAgainst() - match.getScore().getUser1Score());
            result2.setGoalDifference(result2.getGoalFor() - result2.getGoalAgainst());
            result2.setPoints(result2.getWin() * 3 + result2.getDraw());
        }

        if (matchStatus == MatchStatus.READY) {
            matchService.save(match.update(Score.builder().user1Score(0).user2Score(0).build(), matchStatus));
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
            matchService.save(match.update(score, matchStatus));
        }
        joinService.save(join1.update(result1));
        joinService.save(join2.update(result2));
        return "redirect:/room/" + roomId;
    }
}
