package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.score.Score;
import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.room.RoomType;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.MatchDto;
import hongik.ce.jolup.dto.UserDto;
import hongik.ce.jolup.service.*;
import hongik.ce.jolup.dto.RoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("room")
public class RoomController {

    private final RoomService roomService;
    private final JoinService joinService;
    private final UserService userService;
    private final MatchService matchService;

    @GetMapping("/create")
    public String createRoom(@RequestParam(value = "title", defaultValue = "") String title,
                             @RequestParam(value = "roomType", defaultValue = "LEAGUE") RoomType roomType,
                             @RequestParam(value = "memNum", defaultValue = "2") Long memNum,
//                             @RequestParam(value = "emails") List<String> emails,
                             Model model) {
        RoomForm roomForm = new RoomForm();
        roomForm.setTitle(title);
        roomForm.setRoomType(roomType);
        roomForm.setMemNum(memNum);
        for (int i = 0; i < memNum; i++) {
            roomForm.addEmail(new String());
        }

        model.addAttribute("form", roomForm);
        model.addAttribute("roomTypes", RoomType.values());
        return "room/create";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute("form") @Valid RoomForm roomForm,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal User user,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roomTypes", RoomType.values());
            return "room/create";
        }

        for(String email : roomForm.getEmails()) {
            UserDto userDto = userService.findOne(email);
            if (userDto == null) {
                model.addAttribute("data", new Message("존재하지 않는 ID입니다!", "/room/create"));
                return "message";
            }
        }

        if (roomForm.getEmails().size() != roomForm.getEmails().stream().distinct().count()) {
            model.addAttribute("data", new Message("동일한 ID를 입력하실 수 없습니다!", "/room/create"));
            return "message";
        }

        if (roomForm.getRoomType().equals(RoomType.TOURNAMENT) && (roomForm.getMemNum() & (roomForm.getMemNum() - 1)) != 0) {
            model.addAttribute("data", new Message("인원수가 올바르지 않습니다!", "/room/create"));
            return "message";
        }

        RoomDto roomRequestDto = RoomDto.builder()
                .title(roomForm.getTitle())
                .roomType(roomForm.getRoomType())
                .memNum(roomForm.getMemNum()).build();
        Long roomId = roomService.saveRoom(roomRequestDto);

        RoomDto roomDto = roomService.getRoom(roomId);

        for(String email : roomForm.getEmails()) {
            UserDto userDto = userService.findOne(email);
            JoinDto joinDto = JoinDto.builder()
                    .userDto(userDto)
                    .roomDto(roomDto)
                    .result(Result.builder().plays(0).win(0).draw(0).lose(0).goalFor(0)
                            .goalAgainst(0).goalDifference(0).points(0).build())
                    .build();
            joinService.saveJoin(joinDto);
        }

        List<UserDto> userDtoList = joinService.findByRoom(roomDto)
                .stream().map(JoinDto::getUserDto).collect(Collectors.toList());

        if (roomDto.getRoomType().equals(RoomType.LEAGUE)) {
            // 리그
            Collections.shuffle(userDtoList);
            int count = userDtoList.size();

            if (count % 2 == 1) {
                for (int i = 0; i < count; i++) {
                    Long matchNo = 1L;
                    for (int j = 0; j < count/2; j++) {
                        MatchDto matchDto = MatchDto.builder().roomDto(roomDto)
                                .user1Dto(userDtoList.get((i + j) % count))
                                .user2Dto(userDtoList.get((i + count - j - 2) % count))
                                .matchStatus(MatchStatus.READY)
                                .matchNo(matchNo)
                                .roundNo(i + 1L)
                                .score(Score.builder().user1Score(0).user2Score(0).build()).build();
                        matchService.saveMatch(matchDto);
                        matchNo++;
                    }
                }
            }
            else {
                UserDto fixed = userDtoList.remove(0);
                for (int i = 0; i < count - 1; i++) {
                    Long matchNo = 1L;
                    for (int j = 0; j < count/2 - 1; j++) {
                        MatchDto matchDto = MatchDto.builder().roomDto(roomDto)
                                .user1Dto(userDtoList.get((i + j) % (count - 1)))
                                .user2Dto(userDtoList.get((i + count - j - 2) % (count - 1)))
                                .matchStatus(MatchStatus.READY)
                                .matchNo(matchNo)
                                .roundNo(i + 1L)
                                .score(Score.builder().user1Score(0).user2Score(0).build()).build();
                        matchService.saveMatch(matchDto);
                        matchNo++;
                    }
                    MatchDto matchDto = MatchDto.builder().roomDto(roomDto)
                            .user1Dto(userDtoList.get((i + count/2 - 1) % (count - 1)))
                            .user2Dto(fixed)
                            .matchStatus(MatchStatus.READY)
                            .matchNo(matchNo)
                            .roundNo(i + 1L)
                            .score(Score.builder().user1Score(0).user2Score(0).build()).build();
                    matchService.saveMatch(matchDto);
                    matchNo++;
                }
            }
        }
        else if (roomDto.getRoomType().equals(RoomType.TOURNAMENT)) {
            // 토너먼트
            Collections.shuffle(userDtoList);
            int count = userDtoList.size();
            /*Long matchNo = 1L;
            for (int i = 0; i < count / 2; i++) {
                MatchDto matchDto = MatchDto.builder().roomDto(roomDto)
                        .user1Dto(userDtoList.remove(0))
                        .user2Dto(userDtoList.remove(0))
                        .matchStatus(MatchStatus.READY)
                        .matchNo(matchNo)
                        .roundNo(i + 1L)
                        .score(Score.builder().user1Score(0).user2Score(0).build()).build();
                matchService.saveMatch(matchDto);
                matchNo++;
            }*/
            int round = (int)Math.ceil(Math.log(count) / Math.log(2));
            int cnt = count;
            for (int i = 0; i < round; i++) {
                Long matchNo = 1L;
                int temp = cnt / 2;
                for (int j = 0; j < temp; j++) {
                    if (userDtoList.size() >= 2) {
                        MatchDto matchDto = MatchDto.builder().roomDto(roomDto)
                                .user1Dto(userDtoList.remove(0))
                                .user2Dto(userDtoList.remove(0))
                                .matchStatus(MatchStatus.READY)
                                .matchNo(matchNo)
                                .roundNo(i + 1L)
                                .score(Score.builder().user1Score(0).user2Score(0).build()).build();
                        matchService.saveMatch(matchDto);
                        matchNo++;
                    }
                    else {
                        MatchDto matchDto = MatchDto.builder().roomDto(roomDto)
                                .user1Dto(null)
                                .user2Dto(null)
                                .matchStatus(MatchStatus.READY)
                                .matchNo(matchNo)
                                .roundNo(i + 1L)
                                .score(Score.builder().user1Score(0).user2Score(0).build()).build();
                        matchService.saveMatch(matchDto);
                        matchNo++;
                    }
                }
                cnt = (int)Math.ceil(cnt / 2);
            }
        }
        return "redirect:/room/list";
    }

    @GetMapping({"", "/list"})
    public String myRoomList(Model model, @AuthenticationPrincipal User user) {
        List<JoinDto> joins = joinService.findByUser(user.toDto());
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
        return "redirect:/room/list";
    }

    @GetMapping("/{roomId}")
    public String detail(@PathVariable("roomId") Long roomId, Model model, @AuthenticationPrincipal User user) {
        RoomDto roomDto = roomService.getRoom(roomId);
        if (roomDto == null) {
            return "error";
        }

        List<Long> list = joinService.findByRoom(roomDto).stream().map(JoinDto::getUserDto).map(UserDto::getId).collect(Collectors.toList());
        if (!list.contains(user.toDto().getId())) {
            return "error";
        }

        List<MatchDto> matchDtos = matchService.findByRoom(roomDto);
        List<JoinDto> joinDtos = joinService.findByRoomSort(roomDto);
        model.addAttribute("joinDtos", joinDtos);
        model.addAttribute("roomDto", roomDto);
        model.addAttribute("matchDtos", matchDtos);
        if (roomDto.getRoomType().equals(RoomType.LEAGUE)) {
            return "/room/league";
        }
        else if (roomDto.getRoomType().equals(RoomType.TOURNAMENT)) {
            return "/room/tournament";
        }
        return "error";
    }
}
