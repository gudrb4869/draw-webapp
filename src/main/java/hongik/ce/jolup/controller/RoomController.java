package hongik.ce.jolup.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    public String createRoom(@RequestParam(value = "count", defaultValue = "2") Integer count,
                             Model model) {
        JoinForm joinForm = new JoinForm();

        for (int i = 0; i < count; i++) {
            joinForm.addEmail(new String());
        }

        model.addAttribute("form", joinForm);
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("count", count);
        return "room/create";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute("form") JoinForm joinForm,
                             RoomDto roomDto,
                             @AuthenticationPrincipal User user) {

        if (joinForm.getEmails().size() != joinForm.getEmails().stream().distinct().count()) {
            return "redirect:/room/create";
        }

        for(String email : joinForm.getEmails()) {
            UserDto userDto = userService.findOne(email);
            if (userDto == null) {
                throw new IllegalStateException("존재하지 않는 아이디입니다!");
            }
        }

        Long roomId = roomService.save(roomDto);

        for(String email : joinForm.getEmails()) {
            UserDto userDto = userService.findOne(email);
            joinService.save(userDto.getId(), roomId);
        }

        List<Long> userIdList = joinService.findByRoom(roomService.findRoom(roomId))
                .stream().map(JoinDto::getUserDto).collect(Collectors.toList())
                .stream().map(UserDto::getId).collect(Collectors.toList());

        Collections.shuffle(userIdList);
        int count = userIdList.size();

        if (count % 2 == 1) {
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < count/2; j++) {
                    matchService.save(roomId, userIdList.get((i + j) % count),
                            userIdList.get((i + count - j - 2) % count));
                }
            }
        }
        else {
            Long fixed = userIdList.remove(0);
            for (int i = 0; i < count - 1; i++) {
                for (int j = 0; j < count/2 - 1; j++) {
                    matchService.save(roomId, userIdList.get((i + j) % (count - 1)),
                            userIdList.get((i + count - j - 2) % (count - 1)));
                }
                matchService.save(roomId, userIdList.get((i + count/2 - 1) % (count - 1)), fixed);
            }
        }


        return "redirect:/room/list";
    }

    @GetMapping({"", "/list"})
    public String myRoomList(Model model, @AuthenticationPrincipal User user) {
        List<JoinDto> joins = joinService.findByUser(user);
//        model.addAttribute("user", user);
        model.addAttribute("joins", joins);
        return "room/list";
    }

    @DeleteMapping("/{no}")
    public String deleteRoom(@PathVariable("no") Long no) {
        List<Long> matchList = matchService.findByRoom(roomService.findRoom(no))
                .stream().map(MatchDto::getId).collect(Collectors.toList());
        List<Long> joinList = joinService.findByRoom(roomService.findRoom(no))
                .stream().map(JoinDto::getId).collect(Collectors.toList());
        for (Long matchId : matchList)
            matchService.delete(matchId);
        for (Long joinId : joinList)
            joinService.delete(joinId);
        roomService.delete(no);
        return "redirect:/room/list";
    }

    @GetMapping("/{no}")
    public String detail(@PathVariable("no") Long no, Model model) {
        RoomDto roomDto = roomService.findRoom(no);
        List<MatchDto> matchDtos = matchService.findByRoom(roomDto);
        List<JoinDto> joinDtos = joinService.findByRoomSort(roomDto);
        model.addAttribute("joinDtos", joinDtos);
        model.addAttribute("roomDto", roomDto);
        model.addAttribute("matchDtos", matchDtos);
        return "room/detail";
    }
}
