package hongik.ce.jolup.module.match.endpoint;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.match.application.MatchService;
import hongik.ce.jolup.module.competition.application.CompetitionService;
import hongik.ce.jolup.module.match.endpoint.form.DateForm;
import hongik.ce.jolup.module.match.endpoint.form.LocationForm;
import hongik.ce.jolup.module.match.endpoint.form.MatchForm;
import hongik.ce.jolup.module.match.endpoint.form.ScoreForm;
import hongik.ce.jolup.module.account.support.CurrentAccount;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
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
                            @CurrentAccount Account account, Model model) {
        Room room = roomService.getRoom(account, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        model.addAttribute(account);
        model.addAttribute(room);
        model.addAttribute(competition);
        model.addAttribute(match);
        model.addAttribute(LocationForm.from(match));
        return "match/view";
    }

    @PostMapping("/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateMatchInfo(@PathVariable Long matchId, @PathVariable Long roomId, @PathVariable Long competitionId,
                                @CurrentAccount Account account, @RequestBody @Valid MatchForm matchForm) {
        log.info("matchForm = {}", matchForm);
        Room room = roomService.getRoomToUpdate(account, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        matchService.update(match, matchForm, competition);
    }

    @PostMapping("/{matchId}/score")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ScoreForm> updateMatchScore(@PathVariable Long matchId, @PathVariable Long roomId, @PathVariable Long competitionId,
                                                      @CurrentAccount Account account, @RequestBody @Valid ScoreForm scoreForm, Errors errors) {

        log.info("scoreForm = {}", scoreForm);
        if (errors.hasErrors()) {
            log.info("valid error: {}", errors.getErrorCount());
            return ResponseEntity.badRequest().build();
        }
        Room room = roomService.getRoomToUpdate(account, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        matchService.updateScore(match, scoreForm, competition);
        return ResponseEntity.ok(scoreForm);
    }

    @PostMapping("/{matchId}/date")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DateForm> updateMatchDate(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId,
                                                    @CurrentAccount Account account, @RequestBody DateForm dateForm) {
        log.info("dateForm = {}", dateForm);
        Room room = roomService.getRoomToUpdate(account, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        matchService.updateDate(match, dateForm);
        return ResponseEntity.ok(dateForm);
    }

    @GetMapping("/{matchId}/update-score")
    public String updateScoreForm(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId,
                                  @CurrentAccount Account account, Model model) {
        Room room = roomService.getRoomToUpdate(account, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        model.addAttribute(account);
        model.addAttribute(room);
        model.addAttribute(competition);
        model.addAttribute(match);
        model.addAttribute(ScoreForm.from(match));
        return "match/score";
    }

    @PostMapping("/{matchId}/update-score")
    public String updateScore(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId, @CurrentAccount Account account,
                              @Valid ScoreForm scoreForm, BindingResult bindingResult, Model model, RedirectAttributes attributes) {

        Room room = roomService.getRoomToUpdate(account, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        log.info("matchForm = {}", scoreForm);
        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(room);
            model.addAttribute(competition);
            model.addAttribute(match);
            return "match/score";
        }
        matchService.updateScore(match, scoreForm, competition);
        attributes.addFlashAttribute("message", "경기 결과를 수정했습니다.");
        return "redirect:/rooms/" + room.getId() + "/competitions/" + competition.getId() + "/matches/" + match.getId();
    }

    @GetMapping("/{matchId}/update-date")
    public String updateDateForm(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId,
                                 @CurrentAccount Account account, Model model) {
        Room room = roomService.getRoomToUpdate(account, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        model.addAttribute(account);
        model.addAttribute(room);
        model.addAttribute(competition);
        model.addAttribute(match);
        return "match/date";
    }

    @PostMapping("/{matchId}/update-date")
    public String updateDate(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId, @CurrentAccount Account account,
                             Model model, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartDateTime, RedirectAttributes attributes) {

        Room room = roomService.getRoomToUpdate(account, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        matchService.updateStartDateTime(match, newStartDateTime);
        attributes.addFlashAttribute("message", "경기 시간을 수정했습니다.");
        return "redirect:/rooms/" + room.getId() + "/competitions/" + competition.getId() + "/matches/" + match.getId();
    }

    @GetMapping("/{matchId}/update-location")
    public String updateLocationForm(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId,
                                     @CurrentAccount Account account, Model model) {
        Room room = roomService.getRoomToUpdate(account, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        log.info("match = {}", match);
        model.addAttribute(account);
        model.addAttribute(room);
        model.addAttribute(competition);
        model.addAttribute(match);
        model.addAttribute(LocationForm.from(match));
        return "match/location";
    }

    @PostMapping("/{matchId}/update-location")
    public String updateLocation(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId, @CurrentAccount Account account,
                                 LocationForm locationForm, RedirectAttributes attributes) {
        log.info("locationForm = {}", locationForm);
        Room room = roomService.getRoomToUpdate(account, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        matchService.updateLocation(match, locationForm);
        attributes.addFlashAttribute("message", "경기 장소를 수정했습니다.");
        return "redirect:/rooms/" + room.getId() + "/competitions/" + competition.getId() + "/matches/" + match.getId();
    }
}
