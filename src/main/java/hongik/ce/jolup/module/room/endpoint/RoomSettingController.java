package hongik.ce.jolup.module.room.endpoint;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.support.CurrentMember;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms/{id}/settings")
public class RoomSettingController {

    private final RoomService roomService;

    @GetMapping
    public String viewRoomSetting(@CurrentMember Member member, @PathVariable Long id, Model model) {
        Room room = roomService.getRoomToUpdate(member, id);
        model.addAttribute(member);
        model.addAttribute(room);
        return "room/settings/room";
    }

    @PostMapping("/room/reveal")
    public String revealRoom(@CurrentMember Member member, @PathVariable Long id, RedirectAttributes attributes) {
        Room room = roomService.getRoomToUpdate(member, id);
        roomService.reveal(room);
        attributes.addFlashAttribute("message", "모임을 공개했습니다.");
        return "redirect:/rooms/" + room.getId() + "/settings";
    }

    @PostMapping("/room/conceal")
    public String concealRoom(@CurrentMember Member member, @PathVariable Long id, RedirectAttributes attributes) {
        Room room = roomService.getRoomToUpdate(member, id);
        roomService.conceal(room);
        attributes.addFlashAttribute("message", "모임을 비공개했습니다.");
        return "redirect:/rooms/" + room.getId() + "/settings";
    }

    @PostMapping("/room/title")
    public String updateRoomTitle(@CurrentMember Member member, @PathVariable Long id, String newTitle,
                                  Model model, RedirectAttributes attributes) {
        Room room = roomService.getRoomToUpdate(member, id);
        if (!roomService.isValidTitle(newTitle)) {
            model.addAttribute(member);
            model.addAttribute(room);
            model.addAttribute("roomTitleError", "모임 이름을 다시 입력하세요.");
            return "room/settings/room";
        }
        roomService.updateRoomTitle(room, newTitle);
        attributes.addFlashAttribute("message", "모임 이름을 수정하였습니다.");
        return "redirect:/rooms/" + room.getId() + "/settings";
    }

    @DeleteMapping("/room/remove")
    public String removeRoom(@CurrentMember Member member, @PathVariable Long id) {
        Room room = roomService.getRoomToUpdate(member, id);
        roomService.remove(room);
        return "redirect:/";
    }
}
