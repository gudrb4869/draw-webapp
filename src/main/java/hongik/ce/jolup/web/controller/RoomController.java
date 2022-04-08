package hongik.ce.jolup.web.controller;

import hongik.ce.jolup.domain.accounts.Account;
import hongik.ce.jolup.domain.rooms.Room;
import hongik.ce.jolup.domain.rooms.RoomType;
import hongik.ce.jolup.service.RoomService;
import hongik.ce.jolup.web.dto.RoomSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/makeroom")
    public String makeroom(Model model) {
        model.addAttribute("roomTypes", RoomType.values());
        return "makeroom";
    }

    @PostMapping("/makeroom")
    public String makeroom(RoomSaveRequestDto requestDto, @AuthenticationPrincipal Account account) {
        roomService.creatRoom(requestDto, account);
        return "redirect:/myroom";
    }

    @GetMapping("/myroom")
    public String myRoomList(Model model) {
        List<Room> rooms = roomService.findMyRooms();
        model.addAttribute("rooms", rooms);
        return "myroom";
    }
}
