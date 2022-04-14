package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.room.RoomType;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.MatchDto;
import hongik.ce.jolup.dto.UserDto;
import hongik.ce.jolup.service.JoinService;
import hongik.ce.jolup.service.MatchService;
import hongik.ce.jolup.service.RoomService;
import hongik.ce.jolup.service.UserService;
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

        List<Long> userIdList = joinService.findByRoom(roomService.findRoom(roomId).toEntity())
                .stream().map(JoinDto::getUserDto).collect(Collectors.toList())
                .stream().map(UserDto::getId).collect(Collectors.toList());

        Collections.shuffle(userIdList);

        for (int i = 0; i < userIdList.size(); i++) {
            for (int j = i + 1; j < userIdList.size(); j++) {
                matchService.save(roomId, userIdList.get(i), userIdList.get(j));
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

    @GetMapping("/{no}")
    public String detail(@PathVariable("no") Long no, Model model) {
        RoomDto roomDto = roomService.findRoom(no);
        List<MatchDto> matchDtos = matchService.findByRoom(roomDto);
        model.addAttribute("roomDto", roomDto);
        model.addAttribute("matchDtos", matchDtos);
        return "room/detail";
    }
}
