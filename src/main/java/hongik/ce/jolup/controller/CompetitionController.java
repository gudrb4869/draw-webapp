package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.service.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("rooms/{roomId}/competitions")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final JoinService joinService;
    private final MatchService matchService;
    private final BelongService belongService;

    @GetMapping("/create")
    public String createForm(@PathVariable("roomId") Long roomId,
                             @RequestParam(name = "title", defaultValue = "") String title,
                             @RequestParam(name = "competitionType", defaultValue = "LEAGUE") CompetitionType competitionType,
                             @RequestParam(name = "headCount", defaultValue = "2") Long headCount,
                             @RequestParam(name = "emails", defaultValue = ",") List<String> emails,
                             @AuthenticationPrincipal Member member,
                             Model model) {

        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.ADMIN)) {
            return "error";
        }

        CreateCompetitionForm competitionForm = new CreateCompetitionForm(title, competitionType, headCount);
        for(int i = 0; i < headCount; i++) {
            if (i < emails.size()) {
                competitionForm.addEmail(emails.get(i));
                continue;
            }
            competitionForm.addEmail("");
        }
        log.info("GET: create, competitionForm = {}", competitionForm);
        model.addAttribute("form", competitionForm);
        return "competitions/create";
    }

    @PostMapping("/create")
    public String create(@PathVariable("roomId") Long roomId,
                         @ModelAttribute("form") @Valid CreateCompetitionForm competitionForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal Member member) {

        log.info("POST : createCompetition. competitionForm = {}", competitionForm);

        List<Belong> belongs = belongService.findByRoomId(roomId);
        Belong myBelong = belongs.stream()
                .filter(b -> b.getMember().getId().equals(member.getId())).findFirst().orElse(null);

        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.ADMIN)) {
            return "error";
        }

        if (bindingResult.hasErrors()) {
            return "competitions/create";
        }

        List<String> emails = competitionForm.getEmails();
        if (competitionForm.getHeadCount() != emails.size()) {
            bindingResult.addError(new ObjectError("form", null, null, "대회 참가 인원과 참가자 아이디의 갯수가 일치하지 않습니다."));
            return "competitions/create";
        }

        if (emails.size() != emails.stream().distinct().count()) {
            bindingResult.addError(new ObjectError("form", null, null, "동일한 아이디를 입력할 수 없습니다."));
            return "competitions/create";
        }

        List<Long> memberIds = new ArrayList<>();
        for(int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            Belong belong = belongs.stream()
                    .filter(b -> b.getMember().getEmail().equals(email)).findFirst().orElse(null);
            if (belong == null) {
                bindingResult.addError(new FieldError("form", "emails[" + i + "]", email, false, null, null, "존재하지 않는 회원입니다."));
                return "competitions/create";
            }
            memberIds.add(belong.getMember().getId());
        }

        Long competitionId = competitionService.save(competitionForm.getTitle(), competitionForm.getCompetitionType(), roomId);
        log.info("saveJoin Start");
        joinService.save(memberIds, competitionId);
        log.info("saveJoin End");

        log.info("match Create Start");
        matchService.saveMatches(memberIds, competitionId);
        log.info("match Create Finish");
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{competitionId}")
    public String competitionDetail(@PathVariable("roomId") Long roomId,
                                    @PathVariable("competitionId") Long competitionId,
                                    @AuthenticationPrincipal Member member,
                                    Model model) {

        log.info("GET : CompetitionDetail. competitionId = {}", competitionId);

        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null) {
            return "error";
        }

        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        List<Match> matches = matchService.findByCompetition(competitionId);

        List<Join> joins = joinService.findByCompetitionSort(competitionId);
        Join join = joins.stream().filter(j -> j.getMember().getId().equals(member.getId())).findAny().orElse(null);
        LinkedHashMap<Integer, LinkedHashMap<Integer, Match>> hashMap = new LinkedHashMap<>();
        Integer roundNo = matches.get(matches.size() - 1).getRoundNo();

        for (Match match : matches) {
            hashMap.computeIfAbsent(match.getRoundNo(), k -> new LinkedHashMap<>()).put(match.getMatchNo(), match);
        }

        for (Map.Entry<Integer, LinkedHashMap<Integer, Match>> entry : hashMap.entrySet()) {
            System.out.println("entry = " + entry);
        }

        if (competition.getCompetitionType().equals(CompetitionType.TOURNAMENT)) {
            for (int i = 0; i <= roundNo; i++) {
                for (int j = 0; j < Math.pow(2, i); j++) {
                    System.out.println(i + "-" + j + "경기 = " + hashMap.get(i).get(j));
                }
            }
        }

        model.addAttribute("competition", competition);
        model.addAttribute("hashMap", hashMap);
        model.addAttribute("myJoin", join);
        model.addAttribute("myBelong", myBelong);
        model.addAttribute("joins", joins);
        return "competitions/detail";
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

        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.ADMIN)) {
            return "error";
        }

        competitionService.deleteCompetition(competitionId);
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{competitionId}/edit")
    public String updateForm(@PathVariable("roomId") Long roomId,
                             @PathVariable("competitionId") Long competitionId,
                             @AuthenticationPrincipal Member member,
                             Model model) {
        Competition competition = competitionService.findOne(competitionId, roomId);
        log.info("GET : editCompetition, competition = {}", competition);
        if (competition == null) {
            return "error";
        }

        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.ADMIN)) {
            return "error";
        }

        model.addAttribute("form", new UpdateCompetitionForm(competition.getId(), competition.getTitle()));
        return "competitions/update";
    }

    @PostMapping("/{competitionId}/edit")
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

        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.ADMIN)) {
            return "error";
        }

        competitionService.updateCompetition(competitionForm.getId(), competitionForm.getTitle());
        return "redirect:/rooms/{roomId}/competitions/{competitionId}";
    }

    @Getter @Setter
    @NoArgsConstructor @ToString
    private static class CreateCompetitionForm {
        @NotBlank(message = "대회 이름은 필수 입력 값입니다!")
//    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "대회 이름 형식이 올바르지 않습니다.")
        private String title;

        @NotNull(message = "대회 방식은 필수 입력 값입니다!")
        private CompetitionType competitionType;

        @NotNull(message = "대회 참여 인원 수는 필수 입력 값입니다!")
        private Long headCount;

        @Size(min = 2, max = 64, message = "참가자수 범위는 최소 2명부터 최대 64명까지입니다!")
        @NotNull(message = "참가자 목록은 null 값일 수 없습니다!")
        private List<@NotBlank(message = "참가자 아이디는 필수 입력 값입니다!") String> emails = new ArrayList<>();

        public CreateCompetitionForm(String title, CompetitionType competitionType, Long headCount) {
            this.title = title;
            this.competitionType = competitionType;
            this.headCount = headCount;
        }

        public void addEmail(String email) {
            this.emails.add(email);
        }
    }

    @Getter @Setter @AllArgsConstructor
    @NoArgsConstructor @ToString
    private static class UpdateCompetitionForm {

        private Long id;

        @NotBlank(message = "대회 이름은 필수 입력 값입니다.")
        private String title;
    }
}
