package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.room.RoomType;
import hongik.ce.jolup.service.JoinService;
import hongik.ce.jolup.service.RoomService;
import hongik.ce.jolup.service.UserService;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.RoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("room")
public class RoomController {

    private final RoomService roomService;
    private final JoinService joinRoomService;
    private final UserService userService;

    @GetMapping("/create")
    public String createRoom(@RequestParam(value = "count", defaultValue = "2") Integer count,
                             Model model) {
        JoinDto joinRoomDto = new JoinDto();

        for (int i = 0; i < count; i++) {
            joinRoomDto.addEmail(new String());
        }

        model.addAttribute("form", joinRoomDto);
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("count", count);
        return "room/create";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute JoinDto form,
                             RoomDto requestDto,
                             @AuthenticationPrincipal User user) {
        Long roomId = roomService.save(requestDto);
        joinRoomService.createJoinRoom(user, roomId);

        for(String email : form.getEmails()) {
            Optional<User> member = userService.findOne(email);
            if (member != null) {
                joinRoomService.addJoinRoom(member.get(), roomId);
            }
        }

        return "redirect:/room/list";
    }

    @GetMapping({"", "/list"})
    public String myRoomList(Model model, @AuthenticationPrincipal User user) {
        List<Join> joinRooms = joinRoomService.findMyJoinRooms(user);
        model.addAttribute("user", user);
        model.addAttribute("joinRooms", joinRooms);
        return "room/list";
    }

    @GetMapping("/{no}")
    public String detail(@PathVariable("no") Long no, Model model) {
        RoomDto roomDto = roomService.getRoom(no);

        model.addAttribute("roomDto", roomDto);
        return "room/detail";
    }
}
