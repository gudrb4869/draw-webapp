package hongik.ce.jolup.web.controller;

import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.room.JoinRoom;
import hongik.ce.jolup.domain.room.RoomType;
import hongik.ce.jolup.service.JoinRoomService;
import hongik.ce.jolup.service.RoomService;
import hongik.ce.jolup.web.dto.RoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("room")
public class RoomController {

    private final RoomService roomService;
    private final JoinRoomService joinRoomService;

    @GetMapping("/create")
    public String createRoom(@RequestParam(value = "count", defaultValue = "2") Integer count,
                             Model model) {
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("count", count);
        return "room/create";
    }

    @PostMapping("/create")
    public String createRoom(RoomDto requestDto, @AuthenticationPrincipal User user) {
        joinRoomService.createJoinRoom(user, roomService.createRoom(requestDto));
        return "redirect:/room/list";
    }

    @GetMapping({"", "/list"})
    public String myRoomList(Model model, @AuthenticationPrincipal User user) {
        List<JoinRoom> joinRooms = joinRoomService.findMyJoinRooms(user);
        model.addAttribute("joinRooms", joinRooms);
        return "room/list";
    }
}
