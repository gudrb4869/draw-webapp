package hongik.ce.jolup.module.room.endpoint;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.domain.entity.Follow;
import hongik.ce.jolup.module.account.infra.repository.FollowRepository;
import hongik.ce.jolup.module.account.support.CurrentAccount;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.room.endpoint.form.InviteForm;
import hongik.ce.jolup.module.room.endpoint.form.RoomForm;
import hongik.ce.jolup.module.room.endpoint.validator.InviteFormValidator;
import hongik.ce.jolup.module.room.infra.repository.JoinRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final JoinRepository joinRepository;
    private final RoomService roomService;
    private final FollowRepository followRepository;
    private final InviteFormValidator inviteFormValidator;

    @InitBinder("inviteForm")
    public void inviteFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(inviteFormValidator);
    }

    @GetMapping("/create")
    public String createRoomForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new RoomForm());
        return "room/form";
    }

    @PostMapping("/create")
    public String createRoom(@CurrentAccount Account account, @Valid RoomForm roomForm, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "room/form";
        }
        Room room = roomService.createNewRoom(roomForm, account);
        attributes.addFlashAttribute("message", "새로운 방을 만들었습니다.");
        return "redirect:/rooms/" + room.getId();
    }

    @GetMapping("/{id}")
    public String viewRoom(@CurrentAccount Account account, @PathVariable Long id, Model model) {
        Room room = roomService.getRoom(account, id);
        model.addAttribute(account);
        model.addAttribute(room);
        return "room/view";
    }

    @GetMapping("/{id}/update")
    public String updateRoomForm(@CurrentAccount Account account, @PathVariable Long id, Model model) {
        Room room = roomService.getRoomToUpdate(account, id);
        model.addAttribute(account);
        model.addAttribute(RoomForm.from(room));
        return "room/update-form";
    }

    @PostMapping("/{id}/update")
    public String updateRoom(@CurrentAccount Account account, @PathVariable Long id, Model model,
                             @Valid RoomForm roomForm, BindingResult result, RedirectAttributes attributes) {
        Room room = roomService.getRoomToUpdate(account, id);
        if (result.hasErrors()) {
            model.addAttribute(account);
            return "room/update-form";
        }
        roomService.updateRoom(room, roomForm);
        attributes.addFlashAttribute("message", "방을 수정했습니다.");
        return "redirect:/rooms/" + room.getId();
    }

    @GetMapping("/{id}/members")
    public String viewRoomMembers(@CurrentAccount Account account, @PathVariable Long id,
                                  @PageableDefault(size = 10, sort = "grade", direction = Sort.Direction.ASC) Pageable pageable,
                                  Model model) {
        Room room = roomService.getRoom(account, id);
        model.addAttribute(account);
        model.addAttribute(room);
        Page<Join> joins = joinRepository.findByRoom(room, pageable);
        model.addAttribute("joins", joins);
        return "room/members";
    }

    @GetMapping("/{id}/join")
    public String joinRoom(@CurrentAccount Account account, @PathVariable Long id) {
        Room room = roomService.getRoom(account, id);
        roomService.addMember(room, account);
        return "redirect:/rooms/" + room.getId() + "/members";
    }

    @GetMapping("/{id}/leave")
    public String leaveRoom(@CurrentAccount Account account, @PathVariable Long id) {
        Room room = roomService.findOne(id);
        roomService.removeMember(room, account);
        return "redirect:/rooms/" + room.getId() + "/members";
    }

    @GetMapping("/{id}/invite")
    public String inviteForm(@PathVariable Long id, @CurrentAccount Account account, Model model) {
        Room room = roomService.getRoomToUpdate(account, id);
        List<Account> accounts = room.getJoins().stream().map(Join::getAccount)
                .collect(Collectors.toList());
        List<Account> friends = followRepository.findByFollowing(account)
                .stream().map(Follow::getFollower).collect(Collectors.toList())
                .stream().filter(f -> !accounts.contains(f)).collect(Collectors.toList());
        model.addAttribute(account);
        model.addAttribute(room);
        model.addAttribute("members", friends);
        model.addAttribute(new InviteForm());
        return "room/invite-form";
    }

    @PostMapping("/{id}/invite")
    public String invite(@CurrentAccount Account account, @PathVariable Long id, Model model,
                         @Valid InviteForm inviteForm, BindingResult result, RedirectAttributes attributes) {
        Room room = roomService.getRoomToUpdate(account, id);
        List<Account> accounts = room.getJoins().stream().map(Join::getAccount).collect(Collectors.toList());
        List<Account> friends = followRepository.findByFollowing(account)
                .stream().map(Follow::getFollower).collect(Collectors.toList())
                .stream().filter(f -> !accounts.contains(f)).collect(Collectors.toList());
        if (result.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(room);
            model.addAttribute("members", friends);
            return "room/invite-form";
        }
        roomService.inviteRoom(friends, room, inviteForm.getMembers());
        attributes.addFlashAttribute("message", "친구들에게 방 초대 요청을 보냈습니다.");
        return "redirect:/rooms/" + room.getId();
    }

    @DeleteMapping("/{id}")
    public String removeRoom(@CurrentAccount Account account, @PathVariable Long id) {
        Room room = roomService.getRoomToUpdate(account, id);
        roomService.remove(room);
        return "redirect:/";
    }
}
