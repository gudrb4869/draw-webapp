package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.dto.MatchDto;
import hongik.ce.jolup.dto.ResultDto;
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

    @GetMapping("/{no}")
    public String matchDetail(@PathVariable("no") Long no, Model model) {
        MatchDto matchDto = matchService.findById(no);
        ResultDto resultDto = Result.toDto(matchDto.getResult());
        model.addAttribute("matchDto", matchDto);
        model.addAttribute("resultDto", resultDto);
        model.addAttribute("matchStatuses", MatchStatus.values());
        return "match/update";
    }

    @PostMapping("/update/{no}")
    public String update(@PathVariable("no") Long no,
                         Result result, MatchStatus matchStatus) {
        Match match = matchService.findById(no).toEntity();

        if (matchStatus == MatchStatus.READY) {
            matchService.save(match.update(Result.builder().user1Score(0).user2Score(0).build(), matchStatus));
        } else {
            matchService.save(match.update(result, matchStatus));
        }
        return "redirect:/room/list";
    }
}
