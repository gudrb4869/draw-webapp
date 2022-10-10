package hongik.ce.jolup.module.match.endpoint;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
import hongik.ce.jolup.module.match.application.MatchService;
import hongik.ce.jolup.module.match.domain.entity.Status;
import hongik.ce.jolup.module.competition.application.CompetitionService;
import hongik.ce.jolup.module.match.endpoint.form.MatchForm;
import hongik.ce.jolup.module.member.support.CurrentMember;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @GetMapping("/{matchId}/update")
    public String updateForm(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId,
                             @CurrentMember Member member, Model model) {
        Room room = roomService.getRoomToUpdate(member, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);
        model.addAttribute(member);
        model.addAttribute(room);
        model.addAttribute(competition);
        model.addAttribute(match);
        model.addAttribute(MatchForm.from(match));
        return "match/update-form";
    }

    /*@PostMapping("/{matchId}/update")
    public String update(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId, @CurrentMember Member member,
                         @Valid UpdateMatchForm form, BindingResult bindingResult, Model model, RedirectAttributes attributes) {

        Room room = roomService.getRoomToUpdate(member, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = competition.getMatches().stream().filter(m -> m.getId().equals(matchId))
                .findAny().orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경기입니다."));

        if (competition.getType().equals(CompetitionType.LEAGUE)) {
            leagueService.update(match.getId(), competitionId, Status.AFTER, form.getHomeScore(), form.getAwayScore(), form.getHomeId(), form.getAwayId());
            Set<Member> members = new HashSet<>();
            List<Match> matches = leagueService.findByCompetitionId(competitionId);
            for (Match m : matches) {
                members.add(m.getHome());
                members.add(m.getAway());
            }
            members.remove(member);
            leagueService.sendAlarm(match, roomId, members);
        } else if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
            tournamentService.update(matchId, competitionId, Status.AFTER, form.getHomeScore(), form.getAwayScore());
            Set<Member> members = new HashSet<>();
            List<Match> matches = leagueService.findByCompetitionId(competitionId);
            for (Match m : matches) {
                members.add(m.getHome());
                members.add(m.getAway());
            }
            members.remove(member);
            tournamentService.sendAlarm(match, roomId, members);
        }
        attributes.addFlashAttribute("message", "경기 결과를 수정했습니다.");
        return "redirect:/rooms/{roomId}/competitions/{competitionId}/matches/{matchId}";
    }

    @PostMapping("/{matchId}/init")
    public String init(@PathVariable("roomId") Long roomId,
                       @PathVariable("competitionId") Long competitionId,
                       @PathVariable("matchId") Long matchId,
                       @AuthenticationPrincipal Member member,
                       RedirectAttributes attributes) {

        log.info("POST initMatch : roomId = {}, competitionId = {}, matchId = {}", roomId, competitionId, matchId);

        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        if (competition.getType().equals(CompetitionType.LEAGUE)) {
            Match match = leagueService.findByIdAndCompetitionId(matchId, competitionId);
            if (match == null) {
                return "error";
            }
            Long homeId = match.getHome() != null ? match.getHome().getId() : null;
            Long awayId = match.getAway() != null ? match.getAway().getId() : null;
            leagueService.update(match.getId(), competitionId, Status.AFTER, 0, 0, homeId, awayId);
        } else if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
            Match match = tournamentService.findByIdAndCompetitionId(matchId, competitionId);
            if (match == null) {
                return "error";
            }
            Long homeId = match.getHome() != null ? match.getHome().getId() : null;
            Long awayId = match.getAway() != null ? match.getAway().getId() : null;
            tournamentService.update(matchId, competitionId, Status.BEFORE, 0, 0);
        }
        attributes.addFlashAttribute("message", "경기 결과를 초기화했습니다.");
        return "redirect:/rooms/{roomId}/competitions/{competitionId}/matches/{matchId}";
    }*/

    @ToString @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    private static class UpdateMatchForm {

        @NotNull
        private Long id;

        private Long homeId;
        private String home;
        private String away;
        private Long awayId;

        @Min(value = 0, message = "최소 0점이어야 합니다.")
        @Max(value = 100, message = "최대 100점이어야 합니다.")
        private Integer homeScore;

        @Min(value = 0, message = "최소 0점이어야 합니다.")
        @Max(value = 100, message = "최대 100점이어야 합니다.")
        private Integer awayScore;
    }
}
