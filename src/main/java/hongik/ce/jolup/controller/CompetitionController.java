package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.score.Score;
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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("rooms/{roomId}/competitions")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final JoinService joinService;
    private final MatchService matchService;
    private final BelongService belongService;

    @GetMapping("/create")
    public String createCompetition(@PathVariable("roomId") Long roomId,
                                    @RequestParam(name = "title", defaultValue = "") String title,
                                    @RequestParam(name = "competitionType", defaultValue = "LEAGUE") CompetitionType competitionType,
                                    @RequestParam(name = "headCount", defaultValue = "2") Long headCount,
                                    @RequestParam(name = "emails", defaultValue = ",") List<String> emails,
                                    @AuthenticationPrincipal Member member,
                                    Model model) {

        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        CreateCompetitionForm competitionForm = new CreateCompetitionForm(title, competitionType, headCount);
        for(int i = 0; i < headCount; i++) {
            if (i < emails.size()) {
                competitionForm.addEmail(emails.get(i));
                continue;
            }
            competitionForm.addEmail(new String());
        }
        log.info("GET: create, competitionForm = {}", competitionForm);
        model.addAttribute("form", competitionForm);
        return "competition/create";
    }

    @PostMapping("/create")
    public String createCompetition(@PathVariable("roomId") Long roomId,
                                    @ModelAttribute("form") @Valid CreateCompetitionForm competitionForm,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal Member member) {

        log.info("POST : create, competitionForm = {}", competitionForm);

        List<Belong> Belongs = belongService.findByRoomId(roomId);
        Belong belong = Belongs.stream()
                .filter(b -> b.getMember().getId().equals(member.getId())).findFirst().orElse(null);
        if (belong == null || !belong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        if (bindingResult.hasErrors()) {
            return "competition/create";
        }

        List<String> emails = competitionForm.getEmails();
        if (competitionForm.getHeadCount() != emails.size()) {
            bindingResult.addError(new ObjectError("form", null, null, "대회 참가 인원과 참가자 아이디의 갯수가 일치하지 않습니다."));
            return "competition/create";
        }

        if (emails.size() != emails.stream().distinct().count()) {
            bindingResult.addError(new ObjectError("form", null, null, "동일한 아이디를 입력할 수 없습니다."));
            return "competition/create";
        }

        for(int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            Optional<Belong> findMember = Belongs.stream()
                    .filter(b -> b.getMember().getEmail().equals(email)).findFirst();
            if (findMember.isEmpty()) {
                bindingResult.addError(new FieldError("form", "emails[" + i + "]", email, false, null, null, "존재하지 않는 회원입니다."));
                return "competition/create";
            }
        }

        Long competitionId = competitionService.save(competitionForm.getTitle(), competitionForm.getCompetitionType(), belong.getRoom());
        log.info("saveJoin Start");
        Competition competition = competitionService.findOne(competitionId);
        for (String email : emails) {
            Belong findBelong = Belongs.stream().filter(b -> b.getMember().getEmail().equals(email)).findAny().orElse(null);
            Member findMember = null; 
            if (findBelong != null) {
                findMember = findBelong.getMember();
            }
            Result result = Result.builder().plays(0).win(0).draw(0).lose(0).goalFor(0)
                    .goalAgainst(0).goalDifference(0).points(0).build();
            joinService.save(findMember, competition, result);
        }
        log.info("saveJoin End");

        List<Member> members = joinService.findByCompetitionId(competitionId)
                .stream().map(Join::getMember).collect(Collectors.toList());

        if (competition.getCompetitionType().equals(CompetitionType.LEAGUE)) {
            // 리그
            Collections.shuffle(members);
            int count = members.size();
            int round = count % 2 == 1 ? count : count - 1;
            if (count % 2 == 1) {
                for (int i = 0; i < count; i++) {
                    for (int j = 0; j < count/2; j++) {
                        Match match = Match.builder().competition(competition)
                                .home(members.get((i + j) % count))
                                .away(members.get((i + count - j - 2) % count))
                                .matchStatus(MatchStatus.READY)
                                .roundNo(i)
                                .matchNo(j)
                                .score(Score.builder().homeScore(0).awayScore(0).build()).build();
                        matchService.saveMatch(match);
                    }
                }
            }
            else {
                int j;
                Member fixed = members.remove(0);
                for (int i = 0; i < count - 1; i++) {
                    for (j = 0; j < count/2 - 1; j++) {
                        Match match = Match.builder().competition(competition)
                                .home(members.get((i + j) % (count - 1)))
                                .away(members.get((i + count - j - 2) % (count - 1)))
                                .matchStatus(MatchStatus.READY)
                                .roundNo(i)
                                .matchNo(j)
                                .score(Score.builder().homeScore(0).awayScore(0).build()).build();
                        matchService.saveMatch(match);
                    }
                    Match match = Match.builder().competition(competition)
                            .home(i % 2 == 0 ? members.get((i + j) % (count - 1)) : fixed)
                            .away(i % 2 == 0 ? fixed : members.get((i + j) % (count - 1)))
                            .matchStatus(MatchStatus.READY)
                            .roundNo(i)
                            .matchNo(j)
                            .score(Score.builder().homeScore(0).awayScore(0).build()).build();
                    matchService.saveMatch(match);
                }
            }
        }
        else if (competition.getCompetitionType().equals(CompetitionType.TOURNAMENT)) {
            // 토너먼트
            Collections.shuffle(members);
            int count = members.size(); // 참가 인원 수
            int round_num = (int)Math.ceil(Math.log(count) / Math.log(2)); // 총 라운드
            int match_num = 1;
            int auto_win_num = (int)Math.pow(2, round_num) - count; // 부전승 인원 수

            Set<Integer> set = new HashSet<>(); //
            while (set.size() < auto_win_num) {
                Double value = Math.random() * (int)Math.pow(2, round_num - 1);
                set.add(value.intValue());
            }
            List<Integer> list = new ArrayList<>(set);
            Collections.sort(list);

            for (int i = 0; i < round_num; i++) {
                for (int j = 0; j < match_num; j++) {
                    if (i == round_num - 1 && auto_win_num > 0 && list.contains(j)) {
                        log.info("i, j = {}, {}", i, j);
                        continue;
                    }
                    Match match = Match.builder().competition(competition)
                            .home(i == round_num - 1 ? members.remove(0) :
                                    (i == round_num - 2 && auto_win_num > 0 && list.contains(j * 2) ? members.remove(0) : null))
                            .away(i == round_num - 1 ? members.remove(0) :
                                    (i == round_num - 2 && auto_win_num > 0 && list.contains(j * 2 + 1) ? members.remove(0) : null))
                            .matchStatus(MatchStatus.READY)
                            .roundNo(i)
                            .matchNo(j)
                            .score(Score.builder().homeScore(0).awayScore(0).build()).build();
                    matchService.saveMatch(match);
                }
                match_num *= 2;
            }
        }
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{competitionId}")
    public String detail(@PathVariable("roomId") Long roomId,
                         @PathVariable("competitionId") Long competitionId,
                         @AuthenticationPrincipal Member member,
                         Model model) {

        log.info("CompetitionController GET /{competitionId}");

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

        log.info("matches = {}", matches);
        LinkedHashMap<Integer, List<Match>> hashMap = new LinkedHashMap<>();

        for (Match match : matches) {
            hashMap.computeIfAbsent(match.getRoundNo(), k -> new ArrayList<>()).add(match);
        }

        for (Map.Entry<Integer, List<Match>> entry : hashMap.entrySet()) {
            log.info("test = {}", entry.getValue());
        }

        log.info("final = {}", hashMap.get(0).get(0));

        log.info("hashMap = {}", hashMap);


        model.addAttribute("competition", competition);
        model.addAttribute("hashMap", hashMap);
        model.addAttribute("myJoin", join);
        model.addAttribute("myBelong", myBelong);
        model.addAttribute("joins", joins);
        return "competition/detail";
    }

    @DeleteMapping("/{competitionId}")
    public String deleteCompetition(@PathVariable("roomId") Long roomId,
                                    @PathVariable("competitionId") Long competitionId,
                                    @AuthenticationPrincipal Member member) {

        log.info("CompetitionController DELETE /{competitionId}");

        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        competitionService.deleteCompetition(competitionId);
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{competitionId}/edit")
    public String editCompetition(@PathVariable("roomId") Long roomId,
                                  @PathVariable("competitionId") Long competitionId,
                                  @AuthenticationPrincipal Member member,
                                  Model model) {
        Competition competition = competitionService.findOne(competitionId, roomId);
        log.info("GET : editCompetition, competition = {}", competition);
        if (competition == null) {
            return "error";
        }

        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }
        UpdateCompetitionForm competitionForm = new UpdateCompetitionForm(competition.getId(), competition.getTitle());
        model.addAttribute("form", competitionForm);
        return "competition/edit";
    }

    @PostMapping("/{competitionId}/edit")
    public String edit(@PathVariable("roomId") Long roomId,
                       @PathVariable("competitionId") Long competitionId,
                       @AuthenticationPrincipal Member member,
                       @ModelAttribute("form") @Valid UpdateCompetitionForm competitionForm,
                       BindingResult result) {
        log.info("POST : UpdateCompetitionForm = {}", competitionForm);

        if (result.hasErrors()) {
            return "competition/edit";
        }

        if (competitionService.findOne(competitionId, roomId) == null) {
            return "error";
        }

        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.MASTER)) {
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

        @Size(min = 2, max = 20, message = "최소 인원 오류")
        @NotNull(message = "null 오류")
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
