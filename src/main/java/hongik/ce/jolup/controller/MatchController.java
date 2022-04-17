package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.dto.MatchDto;
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
        model.addAttribute("matchDto", matchDto);
        return "match/detail";
    }

    @PostMapping("/update/{no}")
    public String update(@PathVariable("no") Long no,
            Integer user1Score, Integer user2Score) {
        Match match = matchService.findById(no).toEntity();
        matchService.save(match.update(Result.builder()
                        .user1Score(user1Score)
                        .user2Score(user2Score).build()));
        return "redirect:/room/list";
    }
}
