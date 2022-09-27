package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.join.Grade;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.CompetitionOption;
import hongik.ce.jolup.domain.competition.LeagueGame;
import hongik.ce.jolup.domain.competition.LeagueTable;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.domain.competition.SingleLegGame;
import hongik.ce.jolup.service.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("rooms/{roomId}/competitions")
public class CompetitionController {

    private final MemberService memberService;
    private final CompetitionService competitionService;
    private final JoinService joinService;
    private final LeagueTableService leagueTableService;
    private final LeagueGameService leagueGameService;
    private final SingleLegGameService singleLegGameService;

    @GetMapping("/create")
    public String createForm(@PathVariable("roomId") Long roomId,
                             @AuthenticationPrincipal Member member,
                             Model model) {

        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        CreateCompetitionForm competitionForm = new CreateCompetitionForm();
        log.info("GET: create, competitionForm = {}", competitionForm);
        model.addAttribute("form", competitionForm);
        return "competition/createCompetitionForm";
    }

    @PostMapping("/create")
    public String create(@PathVariable("roomId") Long roomId,
                         @ModelAttribute("form") @Valid CreateCompetitionForm competitionForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal Member member) {

        log.info("POST : createCompetition. competitionForm = {}", competitionForm);

        List<Join> joins = joinService.findByRoomId(roomId);
        Join myJoin = joins.stream()
                .filter(b -> b.getMember().getId().equals(member.getId())).findFirst().orElse(null);

        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        if (bindingResult.hasErrors()) {
            return "competition/createCompetitionForm";
        }

        List<String> emails = competitionForm.getEmails();
        if (competitionForm.getCount() != emails.size()) {
            bindingResult.addError(new ObjectError("form", null, null, "대회 참가 인원과 참가자 아이디의 갯수가 일치하지 않습니다."));
            return "competition/createCompetitionForm";
        }

        if (emails.size() != emails.stream().distinct().count()) {
            bindingResult.addError(new ObjectError("form", null, null, "동일한 아이디를 입력할 수 없습니다."));
            return "competition/createCompetitionForm";
        }

        List<Long> memberIds = new ArrayList<>();
        for(int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            Join join = joins.stream()
                    .filter(b -> b.getMember().getEmail().equals(email)).findFirst().orElse(null);
            if (join == null) {
                bindingResult.addError(new FieldError("form", "emails[" + i + "]", email, false, null, null, "존재하지 않는 회원입니다."));
                return "competition/createCompetitionForm";
            }
            memberIds.add(join.getMember().getId());
        }

        Set<Member> members = memberService.findMembers(emails);
        members.remove(member);
        Competition competition = competitionService.save(competitionForm.getName(), competitionForm.getType(), roomId);
        competitionService.SendAlarm(competition, members);

        Long competitionId = competition.getId();
        if (competitionForm.getType().equals(CompetitionType.LEAGUE)) {
            leagueTableService.save(memberIds, competitionId);
            leagueGameService.save(memberIds, competitionId);
        } else if (competitionForm.getType().equals(CompetitionType.TOURNAMENT)) {
            singleLegGameService.save(memberIds, competitionId);
        }
        return "redirect:/rooms/{roomId}/competitions/" + competitionId;
    }

    @GetMapping("/{competitionId}")
    public String competitionDetail(@PathVariable("roomId") Long roomId,
                                    @PathVariable("competitionId") Long competitionId,
                                    @AuthenticationPrincipal Member member,
                                    @PageableDefault Pageable pageable,
                                    Model model) {

        log.info("GET : CompetitionDetail. competitionId = {}", competitionId);

        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null) {
            return "error";
        }

        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        if (competition.getType().equals(CompetitionType.LEAGUE)) {
            List<LeagueTable> leagueTables = leagueTableService.findByCompetitionSort(competitionId);
            model.addAttribute("league", competition);
            model.addAttribute("myJoin", myJoin);
            model.addAttribute("leagueTables", leagueTables);
            LinkedHashMap<Integer, List<LeagueGame>> hashMap = new LinkedHashMap<>();
            Page<LeagueGame> leagueGames = leagueGameService.findByCompetitionId(competitionId, leagueTables.size(), pageable);
            log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
                    leagueGames.getTotalElements(), leagueGames.getTotalPages(), leagueGames.getSize(),
                    leagueGames.getNumber(), leagueGames.getNumberOfElements());
            for (LeagueGame leagueGame : leagueGames) {
                hashMap.computeIfAbsent(leagueGame.getRound(), k -> new ArrayList<>()).add(leagueGame);
            }
            model.addAttribute("hashMap", hashMap);
            model.addAttribute("matches", leagueGames);
            return "competition/league";
        } else if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
            model.addAttribute("competition", competition);
            model.addAttribute("myJoin", myJoin);
            LinkedHashMap<Integer, LinkedHashMap<Integer, SingleLegGame>> hashMap = new LinkedHashMap<>();
            List<SingleLegGame> matches = singleLegGameService.findByCompetition(competitionId);
            for (SingleLegGame match : matches) {
                hashMap.computeIfAbsent(match.getRound(), k -> new LinkedHashMap<>()).put(match.getNumber(), match);
            }
            model.addAttribute("hashMap", hashMap);
            return "competition/tournament";
        }
        return "redirect:/rooms/{roomId}";
    }

    @DeleteMapping("/{competitionId}")
    public String delete(@PathVariable("roomId") Long roomId,
                         @PathVariable("competitionId") Long competitionId,
                         @AuthenticationPrincipal Member member) {

        log.info("DELETE : competitionId = {}", competitionId);

        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        competitionService.deleteCompetition(competitionId);
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{competitionId}/update")
    public String updateForm(@PathVariable("roomId") Long roomId,
                             @PathVariable("competitionId") Long competitionId,
                             @AuthenticationPrincipal Member member,
                             Model model) {
        Competition competition = competitionService.findOne(competitionId, roomId);
        log.info("GET : editCompetition, competition = {}", competition);
        if (competition == null) {
            return "error";
        }

        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }
        model.addAttribute("form", new UpdateCompetitionForm(competition.getId(), competition.getName()));
        return "competition/updateCompetitionForm";
    }

    @PostMapping("/{competitionId}/update")
    public String update(@PathVariable("roomId") Long roomId,
                         @PathVariable("competitionId") Long competitionId,
                         @AuthenticationPrincipal Member member,
                         @ModelAttribute("form") @Valid UpdateCompetitionForm competitionForm,
                         BindingResult result) {
        log.info("POST : UpdateCompetitionForm = {}", competitionForm);

        if (result.hasErrors()) {
            return "competitions/update";
        }

        if (competitionService.findOne(competitionId, roomId) == null) {
            return "error";
        }

        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        competitionService.updateCompetition(competitionForm.getId(), competitionForm.getName());
        return "redirect:/rooms/{roomId}/competitions/{competitionId}";
    }

    @Getter @Setter @ToString
    private static class CreateCompetitionForm {
        @NotBlank(message = "대회명을 입력하세요.")
//    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "대회 이름 형식이 올바르지 않습니다.")
        private String name;

        @NotNull(message = "대회 방식을 선택하세요.")
        private CompetitionType type;

        @NotNull(message = "대회 옵션을 선택하세요.")
        private CompetitionOption option;

        @Min(value = 2, message = "참가자수는 최소 2명이어야 합니다.")
        @Max(value = 64, message = "참가자수는 최대 64명이어야 합니다.")
        @NotNull(message = "참가자수를 선택하세요.")
        private Long count;

        @NotNull(message = "null 값일 수 없습니다.")
        private List<@NotBlank(message = "참가자 아이디를 입력하세요.") String> emails = new ArrayList<>();

        public CreateCompetitionForm() {
            this.type = CompetitionType.LEAGUE;
            this.option = CompetitionOption.SINGLE;
            this.count = 2L;
            this.emails.add("");
            this.emails.add("");
        }
    }

    @Getter @Setter @AllArgsConstructor
    @NoArgsConstructor @ToString
    private static class UpdateCompetitionForm {
        @NotNull(message = "대회고유번호는 필수 입력 값입니다.")
        private Long id;

        @NotBlank(message = "대회명은 필수 입력 값입니다.")
        private String name;
    }
}
