package hongik.ce.jolup.module.room.endpoint;

import hongik.ce.jolup.module.member.domain.entity.Follow;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.infra.repository.FollowRepository;
import hongik.ce.jolup.module.member.support.CurrentMember;
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
import java.util.Set;
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
    public String createRoomForm(@CurrentMember Member member, Model model) {
        model.addAttribute(member);
        model.addAttribute(new RoomForm());
        return "room/form";
    }

    @PostMapping("/create")
    public String createRoom(@CurrentMember Member member, @Valid RoomForm roomForm, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "room/form";
        }
        Room room = roomService.createNewRoom(roomForm, member);
        attributes.addFlashAttribute("message", "새로운 모임을 만들었습니다.");
        return "redirect:/rooms/" + room.getId();
    }

    @GetMapping("/{id}")
    public String viewRoom(@CurrentMember Member member, @PathVariable Long id, Model model) {
        Room room = roomService.getRoom(id);
        model.addAttribute(member);
        model.addAttribute(room);
        return "room/view";
    }

    @GetMapping("/{id}/members")
    public String viewRoomMembers(@CurrentMember Member member, @PathVariable Long id,
                                  @PageableDefault(size = 10, sort = "grade", direction = Sort.Direction.ASC) Pageable pageable,
                                  Model model) {
        Room room = roomService.getRoom(member, id);
        model.addAttribute(member);
        model.addAttribute(room);
        Page<Join> joins = joinRepository.findByRoomId(id, pageable);
        model.addAttribute("joins", joins);
        log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
                joins.getTotalElements(), joins.getTotalPages(), joins.getSize(),
                joins.getNumber(), joins.getNumberOfElements());
        return "room/members";
    }

    @GetMapping("/{id}/join")
    public String joinRoom(@CurrentMember Member member, @PathVariable Long id) {
        Room room = roomService.getRoom(member, id);
        roomService.addMember(room, member);
        return "redirect:/rooms/" + room.getId() + "/members";
    }

    @GetMapping("/{id}/leave")
    public String leaveRoom(@CurrentMember Member member, @PathVariable Long id) {
        Room room = roomService.findOne(id);
        roomService.removeMember(room, member);
        return "redirect:/rooms/" + room.getId() + "/members";
    }

    @GetMapping("/{id}/invite")
    public String inviteForm(@PathVariable Long id, @CurrentMember Member member, Model model) {
        Room room = roomService.getRoomToUpdate(member, id);
        List<Member> members = room.getJoins().stream().map(Join::getMember)
                .collect(Collectors.toList());
        List<Member> friends = followRepository.findByFollowing(member)
                .stream().map(Follow::getFollower).collect(Collectors.toList())
                .stream().filter(f -> !members.contains(f)).collect(Collectors.toList());
        model.addAttribute(member);
        model.addAttribute(room);
        model.addAttribute("members", friends);
        model.addAttribute(new InviteForm());
        return "room/invite-form";
    }

    @PostMapping("/{id}/invite")
    public String invite(@CurrentMember Member member, @PathVariable Long id, Model model,
                         @Valid InviteForm inviteForm, BindingResult result, RedirectAttributes attributes) {
        Room room = roomService.getRoomToUpdate(member, id);
        List<Member> members = room.getJoins().stream().map(Join::getMember).collect(Collectors.toList());
        List<Member> friends = followRepository.findByFollowing(member)
                .stream().map(Follow::getFollower).collect(Collectors.toList())
                .stream().filter(f -> !members.contains(f)).collect(Collectors.toList());
        if (result.hasErrors()) {
            model.addAttribute(member);
            model.addAttribute(room);
            model.addAttribute("members", friends);
            return "room/invite-form";
        }
        roomService.inviteRoom(friends, room, inviteForm.getMembers());
        attributes.addFlashAttribute("message", "친구들에게 모임 초대 요청을 보냈습니다.");
        return "redirect:/rooms/" + room.getId();
    }
}
