package hongik.ce.jolup.module.match.endpoint;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.match.application.MatchService;
import hongik.ce.jolup.module.competition.application.CompetitionService;
import hongik.ce.jolup.module.match.endpoint.form.DateForm;
import hongik.ce.jolup.module.match.endpoint.form.ScoreForm;
import hongik.ce.jolup.module.account.support.CurrentUser;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms/{roomId}/competitions/{competitionId}/matches/{matchId}")
public class MatchController {

    private final RoomService roomService;
    private final CompetitionService competitionService;
    private final MatchService matchService;

    @GetMapping
    public String viewMatch(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId,
                            @CurrentUser Account account, Model model) {
        Room room = roomService.getRoom(account, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        model.addAttribute(account);
        model.addAttribute(room);
        model.addAttribute(competition);
        model.addAttribute(match);
        return "match/view";
    }

    @PostMapping("/score")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ScoreForm> updateMatchScore(@PathVariable Long matchId, @PathVariable Long roomId, @PathVariable Long competitionId,
                                                      @CurrentUser Account account, @RequestBody @Valid ScoreForm scoreForm, Errors errors) {

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

    @PostMapping("/date")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DateForm> updateMatchDate(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId,
                                                    @CurrentUser Account account, @RequestBody DateForm dateForm) {
        log.info("dateForm = {}", dateForm);
        Room room = roomService.getRoomToUpdate(account, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        matchService.updateDate(match, dateForm);
        return ResponseEntity.ok(dateForm);
    }
}
