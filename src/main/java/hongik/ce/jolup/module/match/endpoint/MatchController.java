package hongik.ce.jolup.module.match.endpoint;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.match.application.MatchService;
import hongik.ce.jolup.module.competition.application.CompetitionService;
import hongik.ce.jolup.module.match.domain.entity.Status;
import hongik.ce.jolup.module.match.endpoint.form.MatchForm;
import hongik.ce.jolup.module.member.support.CurrentMember;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

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

    @PostMapping("/{matchId}/update")
    public String update(@PathVariable Long roomId, @PathVariable Long competitionId, @PathVariable Long matchId, @CurrentMember Member member,
                         @Valid MatchForm matchForm, BindingResult bindingResult, Model model, RedirectAttributes attributes) {

        Room room = roomService.getRoomToUpdate(member, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        Match match = matchService.getMatch(competition, matchId);

        log.info("matchForm = {}", matchForm);
        if (bindingResult.hasErrors()) {
            model.addAttribute(member);
            model.addAttribute(room);
            model.addAttribute(competition);
            model.addAttribute(match);
            return "match/update-form";
        }

        matchService.updateMatch(competition, match, matchForm);

        /*if (competition.getType().equals(CompetitionType.LEAGUE)) {
            leagueService.update(match.getId(), competitionId, Status.AFTER, matchForm.getHomeScore(), matchForm.getAwayScore(), matchForm.getHomeId(), matchForm.getAwayId());
            Set<Member> members = new HashSet<>();
            List<Match> matches = leagueService.findByCompetitionId(competitionId);
            for (Match m : matches) {
                members.add(m.getHome());
                members.add(m.getAway());
            }
            members.remove(member);
            leagueService.sendAlarm(match, roomId, members);
        } else if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
            tournamentService.update(matchId, competitionId, Status.AFTER, matchForm.getHomeScore(), matchForm.getAwayScore());
            Set<Member> members = new HashSet<>();
            List<Match> matches = leagueService.findByCompetitionId(competitionId);
            for (Match m : matches) {
                members.add(m.getHome());
                members.add(m.getAway());
            }
            members.remove(member);
            tournamentService.sendAlarm(match, roomId, members);
        }*/
        attributes.addFlashAttribute("message", "경기 결과를 수정했습니다.");
        return "redirect:/rooms/" + room.getId() + "/competitions/" + competition.getId() + "/matches/" + match.getId();
    }
}
