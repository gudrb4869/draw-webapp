/*package hongik.ce.jolup.controller;

import hongik.ce.jolup.service.JoinService;
import hongik.ce.jolup.service.RoomService;
import hongik.ce.jolup.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class JoinController {

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
}*/
