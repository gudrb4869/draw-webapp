package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.join.JoinRole;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.score.Score;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.MatchDto;
import hongik.ce.jolup.dto.MemberDto;
import hongik.ce.jolup.service.*;
import hongik.ce.jolup.dto.CompetitionDto;
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

    @GetMapping("/create")
    public String createCompetition(@PathVariable("roomId") Long roomId,
                                    @RequestParam(name = "title", defaultValue = "") String title,
                                    @RequestParam(name = "competitionType", defaultValue = "LEAGUE") CompetitionType competitionType,
                                    @RequestParam(name = "headCount", defaultValue = "2") Long headCount,
                                    @RequestParam(name = "emails", defaultValue = ",") List<String> emails,
                                    Model model) {
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

        log.info("competitionForm = {}", competitionForm);

        if (competitionForm.getHeadCount() != competitionForm.getEmails().size()) {
            bindingResult.addError(new ObjectError("form", null, null, "오류가 발생했습니다."));
        }

        if (bindingResult.hasErrors()) {
            return "competition/create";
        }

        for(String email : competitionForm.getEmails()) {
            MemberDto memberDto = memberService.findOne(email);
            if (memberDto == null) {
                model.addAttribute("data", new Message("존재하지 않는 ID입니다!", "/competition/create"));
                return "message";
            }
        }

        if (competitionForm.getEmails().size() != competitionForm.getEmails().stream().distinct().count()) {
            model.addAttribute("data", new Message("동일한 ID를 입력하실 수 없습니다!", "/competition/create"));
            return "message";
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
        for(String email : competitionForm.getEmails()) {
            MemberDto memberDto = memberService.findOne(email);
            JoinDto joinDto = JoinDto.builder()
                    .memberDto(memberDto)
                    .competitionDto(competitionDto)
                    .result(Result.builder().plays(0).win(0).draw(0).lose(0).goalFor(0)
                            .goalAgainst(0).goalDifference(0).points(0).build())
                    .joinRole(JoinRole.GUEST)
                    .build();
            if (memberDto.getId().equals(member.getId()))
                joinDto.setJoinRole(JoinRole.MASTER);
            joinService.saveJoin(joinDto);
        }

        List<MemberDto> userDtoList = joinService.findByCompetition(competitionDto)
                .stream().map(JoinDto::getMemberDto).collect(Collectors.toList());

//        MatchDto[][] matrix = new MatchDto[competitionDto.getMemNum().intValue()][competitionDto.getMemNum().intValue()];

        if (competitionDto.getCompetitionType().equals(CompetitionType.LEAGUE)) {
            // 리그
            Collections.shuffle(userDtoList);
            int count = userDtoList.size();
            int round = count % 2 == 1 ? count : count - 1;
            if (count % 2 == 1) {
                for (int i = 0; i < count; i++) {
                    for (int j = 0; j < count/2; j++) {
                        MatchDto matchDto = MatchDto.builder().competitionDto(competitionDto)
                                .homeDto(userDtoList.get((i + j) % count))
                                .awayDto(userDtoList.get((i + count - j - 2) % count))
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
                MemberDto fixed = userDtoList.remove(0);
                for (int i = 0; i < count - 1; i++) {
                    for (j = 0; j < count/2 - 1; j++) {
                        MatchDto matchDto = MatchDto.builder().competitionDto(competitionDto)
                                .homeDto(userDtoList.get((i + j) % (count - 1)))
                                .awayDto(userDtoList.get((i + count - j - 2) % (count - 1)))
                                .matchStatus(MatchStatus.READY)
                                .roundNo(i)
                                .matchNo(j)
                                .score(Score.builder().homeScore(0).awayScore(0).build()).build();
                        matchService.saveMatch(matchDto);
                    }
                    MatchDto matchDto = MatchDto.builder().competitionDto(competitionDto)
                            .homeDto(i % 2 == 0 ? userDtoList.get((i + j) % (count - 1)) : fixed)
                            .awayDto(i % 2 == 0 ? fixed : userDtoList.get((i + j) % (count - 1)))
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
            Collections.shuffle(userDtoList);
            int count = userDtoList.size();
            int round = (int)Math.ceil(Math.log(count) / Math.log(2));
//            int round = 0;
//            int match = 0;
            int match = (int) Math.ceil(Math.pow(2, round - 1));
            int auto_win_num = (int)Math.pow(2, round) - count;

            List<MemberDto> autoWinList = new ArrayList<>();
            for (int i = 0; i < auto_win_num; i++) {
                autoWinList.add(userDtoList.remove(0));
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
                            .homeDto(i == 0 ? userDtoList.remove(0) :
                                    (autoWinList.size() > 0 && list.contains(j * 2) ? autoWinList.remove(0) : null))
                            .awayDto(i == 0 ? userDtoList.remove(0) :
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

    @GetMapping
    public String Competitions(@PathVariable("roomId") Long roomId,
                               Model model, @AuthenticationPrincipal Member member) {
        List<JoinDto> joins = joinService.findByMember(member.toDto());
        log.info("member = {}", memberService.getMember(member.getId()));
        model.addAttribute("joins", joins);
        return "competition/list";
    }

    @DeleteMapping("/{competitionId}")
    public String deleteCompetition(@PathVariable("roomId") Long roomId,
                                    @PathVariable("competitionId") Long competitionId) {
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

    @GetMapping("/{competitionId}")
    public String detail(@PathVariable("roomId") Long roomId,
                         @PathVariable("competitionId") Long competitionId,
                         @AuthenticationPrincipal Member member,
                         Model model) {
        CompetitionDto competitionDto = competitionService.getCompetition(competitionId);
        if (competitionDto == null) {
            return "error";
        }

        List<Long> list = joinService.findByCompetition(competitionDto).stream().map(JoinDto::getMemberDto).map(MemberDto::getId).collect(Collectors.toList());
        if (!list.contains(member.toDto().getId())) {
            return "error";
        }

        List<MatchDto> matchDtos = matchService.findByCompetition(competitionDto);

        JoinDto myJoinDto = joinService.findOne(member.toDto(), competitionDto);
        log.info("myJoinDto = {}", myJoinDto);
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
}
