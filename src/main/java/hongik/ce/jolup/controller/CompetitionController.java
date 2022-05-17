package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.join.JoinRole;
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
@RequestMapping("room/{roomId}/competition")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final JoinService joinService;
    private final MemberService memberService;
    private final MatchService matchService;
    private final RoomService roomService;
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
        if (myBelongDto == null || myBelongDto.getBelongType().equals(BelongType.USER)) {
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
        log.info("form = {}", competitionForm);
        model.addAttribute("form", competitionForm);
        return "competition/create";
    }

    @PostMapping("/create")
    public String createCompetition(@PathVariable("roomId") Long roomId,
                                    @ModelAttribute("form") @Valid CompetitionForm competitionForm,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal Member member,
                                    Model model) {

        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null || myBelongDto.getBelongType().equals(BelongType.USER)) {
            return "error";
        }

        log.info("competitionForm = {}", competitionForm);
        List<String> emails = competitionForm.getEmails();
        if (competitionForm.getHeadCount() != emails.size()) {
            bindingResult.addError(new ObjectError("form", null, null, "오류가 발생했습니다1."));
            return "competition/create";
        }

        Boolean flag = false;

        for(int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            MemberDto memberDto = memberService.findOne(email);
            if (email.equals(member.getEmail())) {
                flag = true;
            }
            if (memberDto == null) {
                bindingResult.addError(new FieldError("form", "emails[" + i + "]", email, false, null, null, "존재하지 않는 회원입니다."));
                continue;
            }
            BelongDto belongDto = belongService.findOne(memberDto.getId(), roomId);
            if (belongDto == null) {
                bindingResult.addError(new FieldError("form", "emails[" + i + "]", email, false, null, null, "존재하지 않는 회원입니다."));
                continue;
            }
        }

        if (!flag) {
            bindingResult.addError(new ObjectError("form", null, null, "오류가 발생했습니다2."));
            return "competition/create";
        }

        if (emails.size() != emails.stream().distinct().count()) {
            bindingResult.addError(new ObjectError("form", null, null, "동일한 아이디를 입력할 수 없습니다."));
            return "competition/create";
        }

        if (bindingResult.hasErrors()) {
            return "competition/create";
        }

        CompetitionDto competitionRequestDto = CompetitionDto.builder()
                .title(competitionForm.getTitle())
                .competitionType(competitionForm.getCompetitionType())
                .headCount(competitionForm.getHeadCount())
                .roomDto(roomService.findOne(roomId))
                .build();
        log.info("competitionRequestDto = {}", competitionRequestDto);
        Long competitionId = competitionService.saveCompetition(competitionRequestDto);

        CompetitionDto competitionDto = competitionService.getCompetition(competitionId);
        log.info("competitionDto = {}", competitionDto);
        for(String email : emails) {
            MemberDto memberDto = memberService.findOne(email);
            BelongDto belongDto = belongService.findOne(memberDto.getId(), roomId);
            JoinDto joinDto = JoinDto.builder()
                    .belongDto(belongDto)
                    .competitionDto(competitionDto)
                    .result(Result.builder().plays(0).win(0).draw(0).lose(0).goalFor(0)
                            .goalAgainst(0).goalDifference(0).points(0).build())
                    .joinRole(JoinRole.USER)
                    .build();
            if (memberDto.getId().equals(member.getId()))
                joinDto.setJoinRole(JoinRole.MASTER);
            joinService.saveJoin(joinDto);
        }

        List<MemberDto> memberDtos = joinService.findByCompetition(competitionDto)
                .stream().map(JoinDto::getBelongDto).collect(Collectors.toList())
                .stream().map(BelongDto::getMemberDto).collect(Collectors.toList());

//        MatchDto[][] matrix = new MatchDto[competitionDto.getMemNum().intValue()][competitionDto.getMemNum().intValue()];

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
            int count = memberDtos.size();
            int round = (int)Math.ceil(Math.log(count) / Math.log(2));
//            int round = 0;
//            int match = 0;
            int match = (int) Math.ceil(Math.pow(2, round - 1));
            int auto_win_num = (int)Math.pow(2, round) - count;

            List<MemberDto> autoWinList = new ArrayList<>();
            for (int i = 0; i < auto_win_num; i++) {
                autoWinList.add(memberDtos.remove(0));
            }

            Set<Integer> set = new HashSet<>();
            while (set.size() < auto_win_num) {
                Double value = Math.random() * match;
                set.add(value.intValue());
            }
            List<Integer> list = new ArrayList<>(set);
            Collections.sort(list);

            for (int i = 0; i < round; i++) {
                for (int j = 0; j < match; j++) {
                    if (i == 0 && list.contains(j)) {
                        continue;
                    }
                    MatchDto matchDto = MatchDto.builder().competitionDto(competitionDto)
                            .homeDto(i == 0 ? memberDtos.remove(0) :
                                    (autoWinList.size() > 0 && list.contains(j * 2) ? autoWinList.remove(0) : null))
                            .awayDto(i == 0 ? memberDtos.remove(0) :
                                    (autoWinList.size() > 0 && list.contains(j * 2 + 1)? autoWinList.remove(0) : null))
                            .matchStatus(MatchStatus.READY)
                            .roundNo(i)
                            .matchNo(j)
                            .score(Score.builder().homeScore(0).awayScore(0).build()).build();
                    matchService.saveMatch(matchDto);
                }
                match /= 2;
            }
        }
        return "redirect:/room/{roomId}";
    }

    /*@GetMapping
    public String Competitions(@PathVariable("roomId") Long roomId,
                               Model model, @AuthenticationPrincipal Member member) {
        List<JoinDto> joins = joinService.findByMember(member.toDto());
        log.info("member = {}", memberService.getMember(member.getId()));
        model.addAttribute("joins", joins);
        return "competition/list";
    }*/

    @GetMapping("/{competitionId}")
    public String detail(@PathVariable("roomId") Long roomId,
                         @PathVariable("competitionId") Long competitionId,
                         @AuthenticationPrincipal Member member,
                         Model model) {

        log.info("CompetitionController GET /{competitionId}");

        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null) {
            return "error";
        }

        CompetitionDto competitionDto = competitionService.findOne(competitionId, roomId);
        if (competitionDto == null) {
            return "error";
        }
        JoinDto myJoinDto = joinService.findOne(myBelongDto.getId(), competitionId);

        List<MatchDto> matchDtos = matchService.findByCompetition(competitionDto);

        model.addAttribute("competitionDto", competitionDto);
        model.addAttribute("matchDtos", matchDtos);
        model.addAttribute("myJoinDto", myJoinDto);

        if (competitionDto.getCompetitionType().equals(CompetitionType.LEAGUE)) {
            List<JoinDto> joinDtos = joinService.findByCompetitionSort(competitionDto);
            model.addAttribute("joinDtos", joinDtos);
            return "competition/league";
        }
        else if (competitionDto.getCompetitionType().equals(CompetitionType.TOURNAMENT)) {
            List<JoinDto> joinDtos = joinService.findByCompetitionOrderByJoinRole(competitionDto);
            model.addAttribute("joinDtos", joinDtos);
            return "competition/tournament";
        }
        return "error";
    }

    @DeleteMapping("/{competitionId}")
    public String deleteCompetition(@PathVariable("roomId") Long roomId,
                                    @PathVariable("competitionId") Long competitionId,
                                    @AuthenticationPrincipal Member member) {

        log.info("CompetitionController DELETE /{competitionId}");

        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null || myBelongDto.getBelongType().equals(BelongType.USER)) {
            return "error";
        }

        CompetitionDto competitionDto = competitionService.findOne(competitionId, roomId);
        if (competitionDto == null) {
            return "error";
        }

        JoinDto myJoinDto = joinService.findOne(myBelongDto.getId(), competitionId);
        if ((!myBelongDto.getBelongType().equals(BelongType.MASTER) && myJoinDto == null) || !myJoinDto.getJoinRole().equals(JoinRole.MASTER)) {
            return "error";
        }

        List<Long> matchList = matchService.findByCompetition(competitionService.getCompetition(competitionId))
                .stream().map(MatchDto::getId).collect(Collectors.toList());
        List<Long> joinList = joinService.findByCompetition(competitionService.getCompetition(competitionId))
                .stream().map(JoinDto::getId).collect(Collectors.toList());
        for (Long matchId : matchList)
            matchService.deleteMatch(matchId);
        for (Long joinId : joinList)
            joinService.deleteJoin(joinId);
        competitionService.deleteCompetition(competitionId);
        return "redirect:/room/{roomId}";
    }
}
