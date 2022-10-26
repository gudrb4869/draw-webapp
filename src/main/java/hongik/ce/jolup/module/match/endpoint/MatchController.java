package hongik.ce.jolup.module.match.endpoint;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.match.application.MatchService;
import hongik.ce.jolup.module.competition.application.CompetitionService;
import hongik.ce.jolup.module.match.endpoint.form.ScoreForm;
import hongik.ce.jolup.module.member.support.CurrentMember;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms/{roomId}/competitions/{competitionId}/matches")
public class MatchController {

    private final RoomService roomService;
    private final CompetitionService competitionService;
    private final MatchService matchService;

    @GetMapping("/{matchId}")
    public String viewMatch(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId,
                              @CurrentMember Member member, Model model) {
        Room room = roomService.getRoom(member, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        model.addAttribute(member);
        model.addAttribute(room);
        model.addAttribute(competition);
        model.addAttribute(match);
        return "match/view";
    }

    @GetMapping("/{matchId}/update-score")
    public String updateScoreForm(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId,
                             @CurrentMember Member member, Model model) {
        Room room = roomService.getRoomToUpdate(member, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        model.addAttribute(member);
        model.addAttribute(room);
        model.addAttribute(competition);
        model.addAttribute(match);
        model.addAttribute(ScoreForm.from(match));
        return "match/update-score-form";
    }

    @PostMapping("/{matchId}/update-score")
    public String updateScore(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId, @CurrentMember Member member,
                              @Valid ScoreForm scoreForm, BindingResult bindingResult, Model model, RedirectAttributes attributes) {

        Room room = roomService.getRoomToUpdate(member, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        log.info("matchForm = {}", scoreForm);
        if (bindingResult.hasErrors()) {
            model.addAttribute(member);
            model.addAttribute(room);
            model.addAttribute(competition);
            model.addAttribute(match);
            return "match/update-score-form";
        }
        matchService.updateScore(competition, match, scoreForm);
        attributes.addFlashAttribute("message", "경기 결과를 수정했습니다.");
        return "redirect:/rooms/" + room.getId() + "/competitions/" + competition.getId() + "/matches/" + match.getId();
    }

    @GetMapping("/{matchId}/update-date")
    public String updateMatchDateForm(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId,
                                      @CurrentMember Member member, Model model) {
        Room room = roomService.getRoomToUpdate(member, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        model.addAttribute(member);
        model.addAttribute(room);
        model.addAttribute(competition);
        model.addAttribute(match);
        return "match/update-date-form";
    }

    @PostMapping("/{matchId}/update-date")
    public String updateDate(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId, @CurrentMember Member member,
                             Model model, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartDateTime, RedirectAttributes attributes) {

        Room room = roomService.getRoomToUpdate(member, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        matchService.updateStartDateTime(match, newStartDateTime);
        attributes.addFlashAttribute("message", "경기 시작 일시를 수정했습니다.");
        return "redirect:/rooms/" + room.getId() + "/competitions/" + competition.getId() + "/matches/" + match.getId();
    }
}
