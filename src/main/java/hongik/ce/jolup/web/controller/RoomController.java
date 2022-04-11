package hongik.ce.jolup.web.controller;

import hongik.ce.jolup.domain.accounts.Account;
import hongik.ce.jolup.domain.rooms.JoinRoom;
import hongik.ce.jolup.domain.rooms.RoomType;
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

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("room")
public class RoomController {

    private final RoomService roomService;
    private final JoinRoomService joinRoomService;

    @GetMapping("/create")
    public String createRoom(Model model) {
        model.addAttribute("roomTypes", RoomType.values());
        return "room/create";
    }

    @PostMapping("/create")
    public String createRoom(RoomDto requestDto, @AuthenticationPrincipal Account account) {
        joinRoomService.createJoinRoom(account, roomService.createRoom(requestDto));
        return "redirect:/room/list";
    }

    @GetMapping({"", "/list"})
    public String myRoomList(Model model, @AuthenticationPrincipal Account account) {
        List<JoinRoom> joinRooms = joinRoomService.findMyJoinRooms(account);
        model.addAttribute("joinRooms", joinRooms);
        return "room/list";
    }
}
