package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.join.Grade;
import hongik.ce.jolup.domain.competition.*;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.service.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms/{roomId}/competitions/{competitionId}/matches")
public class MatchController {

    private final LeagueGameService leagueGameService;
    private final SingleLegGameService singleLegGameService;
    private final LeagueTableService leagueTableService;
    private final JoinService joinService;
    private final CompetitionService competitionService;

    @GetMapping("/{matchId}")
    public String matchDetail(@PathVariable("roomId") Long roomId,
                              @PathVariable("competitionId") Long competitionId,
                              @PathVariable("matchId") Long matchId,
                              @AuthenticationPrincipal Member member,
                              Model model) {

        log.info("GET MatchDetail : roomId = {}, competitionId = {}, matchId = {}", roomId, competitionId, matchId);
        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        if (competition.getType().equals(CompetitionType.LEAGUE)) {
            LeagueGame match = leagueGameService.findByIdAndCompetitionId(matchId, competitionId);

            if (match == null) {
                return "error";
            }
            makeMatchDetail(model, competition, match.getId(), match.getHomeScore(), match.getAwayScore(), match.getHome(), match.getAway());
            return "matches/leagueGameDetail";

        } else if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
            SingleLegGame match = singleLegGameService.findByIdAndCompetitionId(matchId, competitionId);

            if (match == null) {
                return "error";
            }
            makeMatchDetail(model, competition, match.getId(), match.getHomeScore(), match.getAwayScore(), match.getHome(), match.getAway());
            return "matches/singleLegGameDetail";
        }
        return "redirect:/rooms/{roomId}/competitions/{competitionId}";
    }

    private void makeMatchDetail(Model model, Competition competition, Long id, Integer homeScore, Integer awayScore, Member home, Member away) {
        MatchDto matchDto = MatchDto.builder().id(id).homeScore(homeScore).awayScore(awayScore).build();
        if (home != null) {
            matchDto.setHomeId(home.getId());
            matchDto.setHome(home.getEmail() + "(" + home.getName() + ")");
        }
        if (away != null) {
            matchDto.setAwayId(away.getId());
            matchDto.setAway(away.getEmail() + "(" + away.getName() + ")");
        }
        model.addAttribute("competition", competition);
        model.addAttribute("match", matchDto);
        log.info("matchDto={}", matchDto);
    }

    @GetMapping("/{matchId}/update")
    public String updateForm(@PathVariable("roomId") Long roomId,
                             @PathVariable("competitionId") Long competitionId,
                             @PathVariable("matchId") Long matchId,
                             @AuthenticationPrincipal Member member,
                             Model model) {

        log.info("GET updateMatch : roomId = {}, competitionId = {}, matchId = {}", roomId, competitionId, matchId);
        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        if (competition.getType().equals(CompetitionType.LEAGUE)) {
            LeagueGame match = leagueGameService.findByIdAndCompetitionId(matchId, competitionId);
            if (match == null) {
                return "error";
            }
            makeUpdateForm(model, match.getId(), match.getHomeScore(), match.getAwayScore(), match.getHome(), match.getAway());
            return "matches/updateLeagueGameForm";
        } else if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
            SingleLegGame match = singleLegGameService.findByIdAndCompetitionId(matchId, competitionId);
            if (match == null) {
                return "error";
            }
            makeUpdateForm(model, match.getId(), match.getHomeScore(), match.getAwayScore(), match.getHome(), match.getAway());
            return "matches/updateSingleLegGameForm";
        }
        return "redirect:/rooms/{roomId}/competitions/{competitionId}/matches/{matchId}";
    }

    private void makeUpdateForm(Model model, Long id, Integer homeScore, Integer awayScore, Member home, Member away) {
        UpdateMatchForm form = UpdateMatchForm.builder().id(id)
                .homeScore(homeScore).awayScore(awayScore).build();
        if (home != null) {
            form.setHomeId(home.getId());
            form.setHome(home.getEmail() + "(" + home.getName() + ")");
        }
        if (away != null) {
            form.setAwayId(away.getId());
            form.setAway(away.getEmail() + "(" + away.getName() + ")");
        }
        log.info("matchUpdateForm = {}", form);
        model.addAttribute("form", form);
    }

    @PostMapping("/{matchId}/update")
    public String update(@PathVariable("roomId") Long roomId,
                         @PathVariable("competitionId") Long competitionId,
                         @PathVariable("matchId") Long matchId, @AuthenticationPrincipal Member member,
                         @ModelAttribute("form") @Valid UpdateMatchForm form,
                         BindingResult bindingResult) {


        log.info("matchUpdateForm = {}", form);
        if (bindingResult.hasErrors()) {
            return "matches/update";
        }

        log.info("POST updateMatch : roomId = {}, competitionId = {}, matchId = {}", roomId, competitionId, matchId);
        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }
        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        if (competition.getType().equals(CompetitionType.LEAGUE)) {
            LeagueGame match = leagueGameService.findByIdAndCompetitionId(matchId, competitionId);
            if (match == null) {
                return "error";
            }
            leagueTableService.update(form.getHomeId(), form.getAwayId(), competitionId,
                    form.getId(), form.getHomeScore(), form.getAwayScore(), Status.AFTER);
        } else if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
            SingleLegGame match = singleLegGameService.findByIdAndCompetitionId(matchId, competitionId);
            if (match == null) {
                return "error";
            }
            singleLegGameService.update(matchId, competitionId, Status.AFTER, form.getHomeScore(), form.getAwayScore());
        }
        return "redirect:/rooms/{roomId}/competitions/{competitionId}/matches/{matchId}";
    }

    @PostMapping("/{matchId}/init")
    public String init(@PathVariable("roomId") Long roomId,
                       @PathVariable("competitionId") Long competitionId,
                       @PathVariable("matchId") Long matchId,
                       @AuthenticationPrincipal Member member) {

        log.info("POST initMatch : roomId = {}, competitionId = {}, matchId = {}", roomId, competitionId, matchId);

        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }
        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        if (competition.getType().equals(CompetitionType.LEAGUE)) {
            LeagueGame match = leagueGameService.findByIdAndCompetitionId(matchId, competitionId);
            if (match == null) {
                return "error";
            }
            Long homeId = match.getHome() != null ? match.getHome().getId() : null;
            Long awayId = match.getAway() != null ? match.getAway().getId() : null;
            leagueTableService.update(homeId, awayId, competitionId, matchId, 0, 0, Status.BEFORE);
        } else if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
            SingleLegGame match = singleLegGameService.findByIdAndCompetitionId(matchId, competitionId);
            if (match == null) {
                return "error";
            }
            Long homeId = match.getHome() != null ? match.getHome().getId() : null;
            Long awayId = match.getAway() != null ? match.getAway().getId() : null;
            singleLegGameService.update(matchId, competitionId, Status.BEFORE, 0, 0);
        }
        return "redirect:/rooms/{roomId}/competitions/{competitionId}/matches/{matchId}";
    }

    @ToString
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class MatchDto {
        private Long id;

        private Long homeId;
        private String home;
        private String away;
        private Long awayId;

        private Integer homeScore;
        private Integer awayScore;
    }

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
