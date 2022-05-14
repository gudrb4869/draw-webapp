package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.join.JoinRole;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.score.Score;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.RoomType;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.MatchDto;
import hongik.ce.jolup.dto.MemberDto;
import hongik.ce.jolup.service.*;
import hongik.ce.jolup.dto.RoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;
    private final JoinService joinService;
    private final MemberService memberService;
    private final MatchService matchService;

    @GetMapping("/create")
    public String createRoom(@RequestParam(name = "title", defaultValue = "") String title,
                             @RequestParam(name = "roomType", defaultValue = "LEAGUE") RoomType roomType,
                             @RequestParam(name = "headCount", defaultValue = "2") Long headCount,
                             @RequestParam(name = "emails", defaultValue = ",") List<String> emails,
                             Model model) {
        RoomForm roomForm = new RoomForm();
        roomForm.setTitle(title);
        roomForm.setRoomType(roomType);
        roomForm.setHeadCount(headCount);
        for(int i = 0; i < headCount; i++) {
            if (i < emails.size()) {
                roomForm.addEmail(emails.get(i));
                continue;
            }
                roomForm.addEmail(new String());
        }
        log.info("form = {}", roomForm);
        model.addAttribute("form", roomForm);
        return "room/create";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute("form") @Valid RoomForm roomForm,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal Member member,
                             Model model) {

        log.info("roomForm = {}", roomForm);
        if (bindingResult.hasErrors()) {
            return "room/create";
        }

        for(String email : roomForm.getEmails()) {
            MemberDto memberDto = memberService.findOne(email);
            if (memberDto == null) {
                model.addAttribute("data", new Message("존재하지 않는 ID입니다!", "/room/create"));
                return "message";
            }
        }

        if (roomForm.getEmails().size() != roomForm.getEmails().stream().distinct().count()) {
            model.addAttribute("data", new Message("동일한 ID를 입력하실 수 없습니다!", "/room/create"));
            return "message";
        }

        RoomDto roomRequestDto = RoomDto.builder()
                .title(roomForm.getTitle())
                .roomType(roomForm.getRoomType())
                .headCount(roomForm.getHeadCount()).build();
        log.info("roomRequestDto = {}", roomRequestDto);
        Long roomId = roomService.saveRoom(roomRequestDto);

        RoomDto roomDto = roomService.getRoom(roomId);
        log.info("roomDto = {}", roomDto);
        for(String email : roomForm.getEmails()) {
            MemberDto memberDto = memberService.findOne(email);
            JoinDto joinDto = JoinDto.builder()
                    .memberDto(memberDto)
                    .roomDto(roomDto)
                    .result(Result.builder().plays(0).win(0).draw(0).lose(0).goalFor(0)
                            .goalAgainst(0).goalDifference(0).points(0).build())
                    .joinRole(JoinRole.GUEST)
                    .build();
            if (memberDto.getId().equals(member.getId()))
                joinDto.setJoinRole(JoinRole.MASTER);
            joinService.saveJoin(joinDto);
        }

        List<MemberDto> userDtoList = joinService.findByRoom(roomDto)
                .stream().map(JoinDto::getMemberDto).collect(Collectors.toList());

//        MatchDto[][] matrix = new MatchDto[roomDto.getMemNum().intValue()][roomDto.getMemNum().intValue()];

        if (roomDto.getRoomType().equals(RoomType.LEAGUE)) {
            // 리그
            Collections.shuffle(userDtoList);
            int count = userDtoList.size();
            int round = count % 2 == 1 ? count : count - 1;
            if (count % 2 == 1) {
                for (int i = 0; i < count; i++) {
                    for (int j = 0; j < count/2; j++) {
                        MatchDto matchDto = MatchDto.builder().roomDto(roomDto)
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
                        MatchDto matchDto = MatchDto.builder().roomDto(roomDto)
                                .homeDto(userDtoList.get((i + j) % (count - 1)))
                                .awayDto(userDtoList.get((i + count - j - 2) % (count - 1)))
                                .matchStatus(MatchStatus.READY)
                                .roundNo(i)
                                .matchNo(j)
                                .score(Score.builder().homeScore(0).awayScore(0).build()).build();
                        matchService.saveMatch(matchDto);
                    }
                    MatchDto matchDto = MatchDto.builder().roomDto(roomDto)
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
        else if (roomDto.getRoomType().equals(RoomType.TOURNAMENT)) {
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
                    MatchDto matchDto = MatchDto.builder().roomDto(roomDto)
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
        return "redirect:/room";
    }

    @GetMapping
    public String myRoomList(Model model, @AuthenticationPrincipal Member member) {
        List<JoinDto> joins = joinService.findByUser(member.toDto());
        log.info("member = {}", memberService.getMember(member.getId()));
        model.addAttribute("joins", joins);
        return "room/list";
    }

    @DeleteMapping("/{roomId}")
    public String deleteRoom(@PathVariable("roomId") Long roomId) {
        List<Long> matchList = matchService.findByRoom(roomService.getRoom(roomId))
                .stream().map(MatchDto::getId).collect(Collectors.toList());
        List<Long> joinList = joinService.findByRoom(roomService.getRoom(roomId))
                .stream().map(JoinDto::getId).collect(Collectors.toList());
        for (Long matchId : matchList)
            matchService.deleteMatch(matchId);
        for (Long joinId : joinList)
            joinService.deleteJoin(joinId);
        roomService.deleteRoom(roomId);
        return "redirect:/room";
    }

    @GetMapping("/{roomId}")
    public String detail(@PathVariable("roomId") Long roomId, Model model, @AuthenticationPrincipal Member user) {
        RoomDto roomDto = roomService.getRoom(roomId);
        if (roomDto == null) {
            return "error";
        }

        List<Long> list = joinService.findByRoom(roomDto).stream().map(JoinDto::getMemberDto).map(MemberDto::getId).collect(Collectors.toList());
        if (!list.contains(user.toDto().getId())) {
            return "error";
        }

        List<MatchDto> matchDtos = matchService.findByRoom(roomDto);

        JoinDto myJoinDto = joinService.findOne(user.toDto(), roomDto);
        log.info("myJoinDto = {}", myJoinDto);
        model.addAttribute("roomDto", roomDto);
        model.addAttribute("matchDtos", matchDtos);
        model.addAttribute("myJoinDto", myJoinDto);
        if (roomDto.getRoomType().equals(RoomType.LEAGUE)) {
            List<JoinDto> joinDtos = joinService.findByRoomSort(roomDto);
            model.addAttribute("joinDtos", joinDtos);
            return "room/league";
        }
        else if (roomDto.getRoomType().equals(RoomType.TOURNAMENT)) {
            List<JoinDto> joinDtos = joinService.findByRoomOrderByJoinRole(roomDto);
            model.addAttribute("joinDtos", joinDtos);
            return "room/tournament";
        }
        return "error";
    }
}
