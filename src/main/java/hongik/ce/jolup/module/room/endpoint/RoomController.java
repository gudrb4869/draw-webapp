package hongik.ce.jolup.module.room.endpoint;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.infra.repository.MemberRepository;
import hongik.ce.jolup.module.member.support.CurrentMember;
import hongik.ce.jolup.module.room.application.JoinService;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.room.domain.entity.Grade;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final JoinRepository joinRepository;
    private final JoinService joinService;
    private final RoomService roomService;
    private final MemberRepository memberRepository;
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
        joinService.save(member.getId(), room.getId(), Grade.ADMIN);
        attributes.addFlashAttribute("message", "새로운 방을 만들었습니다.");
        return "redirect:/rooms/" + room.getId();
    }

    @GetMapping("/{id}")
    public String viewRoom(@CurrentMember Member member, @PathVariable Long id, Model model) {
        Room room = roomService.getRoom(member, id);
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
        Room room = roomService.findOne(id);
        joinService.addMember(room, member);
        return "redirect:/rooms/" + room.getId() + "/members";
    }

    @GetMapping("/{id}/leave")
    public String leaveRoom(@CurrentMember Member member, @PathVariable Long id) {
        Room room = roomService.findOne(id);
        joinService.removeMember(room, member);
        return "redirect:/rooms/" + room.getId() + "/members";
    }

    /*@GetMapping("/{id}/invite")
    public String inviteForm(@PathVariable Long id, @CurrentMember Member member, Model model) {
        Room room = roomService.getRoomToUpdate(member, id);
        model.addAttribute(room);
        model.addAttribute(new InviteForm());
        return "room/inviteMemberForm";
    }

    @PostMapping("/{id}/invite")
    public String invite(@PathVariable Long id, @Valid InviteForm inviteForm, BindingResult result,
                         @CurrentMember Member member, RedirectAttributes attributes) {
        log.info("inviteForm={}", inviteForm);
        Room room = roomService.getRoomToInvite(member, id);
        if (result.hasErrors()) {
            return "room/inviteMemberForm";
        }
        Set<Member> members = memberRepository.findMembersByNameIn(inviteForm.getNames());
        roomService.inviteRoom(room, members, member.getName());
        attributes.addFlashAttribute("message", "회원들에게 방 초대 요청을 보냈습니다.");
        return "redirect:/rooms/" + room.getId();
    }*/
}
