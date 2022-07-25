package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.BelongType;
//import hongik.ce.jolup.domain.join.JoinRole;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.score.Score;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.dto.*;
import hongik.ce.jolup.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null || !myBelongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        CompetitionForm competitionForm = new CompetitionForm();
        competitionForm.setTitle(title);
        competitionForm.setCompetitionType(competitionType);
        competitionForm.setHeadCount(headCount);
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
                                    @ModelAttribute("form") @Valid CompetitionForm competitionForm,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal Member member,
                                    Model model) {

        log.info("POST : create, competitionForm = {}", competitionForm);

        List<BelongDto> belongDtos = belongService.findByRoomId(roomId);

        Optional<BelongDto> optional = belongDtos.stream()
                .filter(b -> b.getMemberDto().getId().equals(member.getId())).findFirst();
        if (optional.isEmpty() || !optional.get().getBelongType().equals(BelongType.MASTER)) {
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
            Optional<BelongDto> findMember = belongDtos.stream()
                    .filter(b -> b.getMemberDto().getEmail().equals(email)).findFirst();
            if (findMember.isEmpty()) {
                bindingResult.addError(new FieldError("form", "emails[" + i + "]", email, false, null, null, "존재하지 않는 회원입니다."));
                return "competition/create";
            }
        }

        CompetitionDto competitionRequestDto = CompetitionDto.builder()
                .title(competitionForm.getTitle())
                .competitionType(competitionForm.getCompetitionType())
                .roomDto(optional.get().getRoomDto())
                .build();
        Long competitionId = competitionService.saveCompetition(competitionRequestDto);
        CompetitionDto competitionDto = competitionService.findOne(competitionId);
        log.info("saveJoin Start");
        for (String email : emails) {
            JoinDto joinDto = JoinDto.builder()
                    .belongDto(belongDtos.stream().filter(b -> b.getMemberDto().getEmail().equals(email)).findFirst().orElse(null))
                    .competitionDto(competitionDto)
                    .result(Result.builder().plays(0).win(0).draw(0).lose(0).goalFor(0)
                            .goalAgainst(0).goalDifference(0).points(0).build())
                    .build();
            joinService.saveJoin(joinDto);
        }
        log.info("saveJoin End");


        List<MemberDto> memberDtos = joinService.findByCompetition(competitionId)
                .stream().map(JoinDto::getBelongDto).collect(Collectors.toList())
                .stream().map(BelongDto::getMemberDto).collect(Collectors.toList());

        if (competitionDto.getCompetitionType().equals(CompetitionType.LEAGUE)) {
            // 리그
            Collections.shuffle(memberDtos);
            int count = memberDtos.size();
            int round = count % 2 == 1 ? count : count - 1;
            if (count % 2 == 1) {
                for (int i = 0; i < count; i++) {
                    for (int j = 0; j < count/2; j++) {
                        MatchDto matchDto = MatchDto.builder().competitionDto(competitionDto)
                                .homeDto(memberDtos.get((i + j) % count))
                                .awayDto(memberDtos.get((i + count - j - 2) % count))
                                .matchStatus(MatchStatus.READY)
                                .roundNo(i)
                                .matchNo(j)
                                .score(Score.builder().homeScore(0).awayScore(0).build()).build();
                        matchService.saveMatch(matchDto);
                    }
                }
            }
            else {
                int j;
                MemberDto fixed = memberDtos.remove(0);
                for (int i = 0; i < count - 1; i++) {
                    for (j = 0; j < count/2 - 1; j++) {
                        MatchDto matchDto = MatchDto.builder().competitionDto(competitionDto)
                                .homeDto(memberDtos.get((i + j) % (count - 1)))
                                .awayDto(memberDtos.get((i + count - j - 2) % (count - 1)))
                                .matchStatus(MatchStatus.READY)
                                .roundNo(i)
                                .matchNo(j)
                                .score(Score.builder().homeScore(0).awayScore(0).build()).build();
                        matchService.saveMatch(matchDto);
                    }
                    MatchDto matchDto = MatchDto.builder().competitionDto(competitionDto)
                            .homeDto(i % 2 == 0 ? memberDtos.get((i + j) % (count - 1)) : fixed)
                            .awayDto(i % 2 == 0 ? fixed : memberDtos.get((i + j) % (count - 1)))
                            .matchStatus(MatchStatus.READY)
                            .roundNo(i)
                            .matchNo(j)
                            .score(Score.builder().homeScore(0).awayScore(0).build()).build();
                    matchService.saveMatch(matchDto);
                }
            }
        }
        else if (competitionDto.getCompetitionType().equals(CompetitionType.TOURNAMENT)) {
            // 토너먼트
            Collections.shuffle(memberDtos);
            int count = memberDtos.size(); // 참가 인원 수
            int round = (int)Math.ceil(Math.log(count) / Math.log(2)); // 총 라운드
            int match = 1;
            int auto_win_num = (int)Math.pow(2, round) - count; // 부전승 인원 수

            Set<Integer> set = new HashSet<>(); //
            while (set.size() < auto_win_num) {
                Double value = Math.random() * (int)Math.pow(2, round - 1);
                set.add(value.intValue());
            }
            List<Integer> list = new ArrayList<>(set);
            Collections.sort(list);

            for (int i = 0; i < round; i++) {
                for (int j = 0; j < match; j++) {
                    if (i == round - 1 && auto_win_num > 0 && list.contains(j)) {
                        log.info("i, j = {}, {}", i, j);
                        continue;
                    }
                    MatchDto matchDto = MatchDto.builder().competitionDto(competitionDto)
                            .homeDto(i == round - 1 ? memberDtos.remove(0) :
                                    (i == round - 2 && auto_win_num > 0 && list.contains(j * 2) ? memberDtos.remove(0) : null))
                            .awayDto(i == round - 1 ? memberDtos.remove(0) :
                                    (i == round - 2 && auto_win_num > 0 && list.contains(j * 2 + 1) ? memberDtos.remove(0) : null))
                            .matchStatus(MatchStatus.READY)
                            .roundNo(i)
                            .matchNo(j)
                            .score(Score.builder().homeScore(0).awayScore(0).build()).build();
                    matchService.saveMatch(matchDto);
                }
                match *= 2;
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

        CompetitionDto competitionDto = competitionService.findOne(competitionId, roomId);
        if (competitionDto == null) {
            return "error";
        }

        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null) {
            return "error";
        }

        JoinDto myJoinDto = joinService.findOne(myBelongDto.getId(), competitionId);

        List<MatchDto> matchDtos = matchService.findByCompetition(competitionId);

        LinkedHashMap<Integer, List<MatchDto>> hashMap = new LinkedHashMap<>();

        for (MatchDto matchDto : matchDtos) {
            hashMap.computeIfAbsent(matchDto.getRoundNo(), k -> new ArrayList<>()).add(matchDto);
        }

        for (Map.Entry<Integer, List<MatchDto>> entry : hashMap.entrySet()) {
            log.info("test = {}", entry.getValue());
        }

        log.info("final = {}", hashMap.get(0).get(0));

        log.info("hashMap = {}", hashMap);


        model.addAttribute("competitionDto", competitionDto);
//        model.addAttribute("matchDtos", matchDtos);
        model.addAttribute("hashMap", hashMap);
        model.addAttribute("myJoinDto", myJoinDto);
        model.addAttribute("myBelongDto", myBelongDto);
        List<JoinDto> joinDtos = joinService.findByCompetitionSort(competitionId);
        model.addAttribute("joinDtos", joinDtos);
        return "competition/detail";
    }

    @DeleteMapping("/{competitionId}")
    public String deleteCompetition(@PathVariable("roomId") Long roomId,
                                    @PathVariable("competitionId") Long competitionId,
                                    @AuthenticationPrincipal Member member) {

        log.info("CompetitionController DELETE /{competitionId}");

        CompetitionDto competitionDto = competitionService.findOne(competitionId, roomId);
        if (competitionDto == null) {
            return "error";
        }

        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null || !myBelongDto.getBelongType().equals(BelongType.MASTER)) {
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
        CompetitionDto competitionDto = competitionService.findOne(competitionId, roomId);
        log.info("GET : editCompetition, competitionDto = {}", competitionDto);
        if (competitionDto == null) {
            return "error";
        }

        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null || !myBelongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        model.addAttribute("competitionDto", competitionDto);
        return "competition/edit";
    }

    @PutMapping("/{competitionId}/edit")
    public String edit(@PathVariable("roomId") Long roomId,
                       @PathVariable("competitionId") Long competitionId,
                       @AuthenticationPrincipal Member member,
                       @ModelAttribute @Valid CompetitionDto competitionDto,
                       BindingResult result) {
        log.info("PUT : edit, competitionDto = {}", competitionDto);

        if (result.hasErrors()) {
            return "competition/edit";
        }

        if (competitionService.findOne(competitionId, roomId) == null) {
            return "error";
        }

        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null || !myBelongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        competitionService.saveCompetition(competitionDto);
        return "redirect:/rooms/{roomId}/competitions/{competitionId}";
    }
}
