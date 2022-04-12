package hongik.ce.jolup.web.controller;

import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.room.JoinRoom;
import hongik.ce.jolup.domain.room.RoomType;
import hongik.ce.jolup.service.JoinRoomService;
import hongik.ce.jolup.service.RoomService;
import hongik.ce.jolup.service.UserService;
import hongik.ce.jolup.web.dto.JoinRoomDto;
import hongik.ce.jolup.web.dto.RoomDto;
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
    private final JoinRoomService joinRoomService;
    private final UserService userService;

    @GetMapping("/create")
    public String createRoom(@RequestParam(value = "count", defaultValue = "2") Integer count,
                             Model model) {
        JoinRoomDto joinRoomDto = new JoinRoomDto();

        for (int i = 0; i < count; i++) {
            joinRoomDto.addEmail(new String());
        }

        model.addAttribute("form", joinRoomDto);
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("count", count);
        return "room/create";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute JoinRoomDto form,
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
        List<JoinRoom> joinRooms = joinRoomService.findMyJoinRooms(user);
        model.addAttribute("joinRooms", joinRooms);
        return "room/list";
    }
}
