package hongik.ce.jolup.module.room.endpoint;

import hongik.ce.jolup.module.competition.application.CompetitionService;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.member.application.MemberService;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.support.CurrentMember;
import hongik.ce.jolup.module.room.application.JoinService;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.room.domain.entity.Grade;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.room.endpoint.form.RoomForm;
import hongik.ce.jolup.module.room.infra.repository.JoinRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final MemberService memberService;
    private final JoinService joinService;
    private final RoomService roomService;
    private final CompetitionService competitionService;
    private final JoinRepository joinRepository;

    @GetMapping("/create")
    public String createForm(@CurrentMember Member member, Model model) {
        model.addAttribute(member);
        model.addAttribute(new RoomForm());
        return "room/form";
    }

    @PostMapping("/create")
    public String create(@Valid RoomForm roomForm, BindingResult result, @CurrentMember Member member, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "room/form";
        }

        Room room = roomService.createNewRoom(roomForm, member);
        joinService.save(member.getId(), room.getId(), Grade.ADMIN);
        attributes.addFlashAttribute("message", "새로운 방을 만들었습니다.");
        return "redirect:/rooms/" + room.getId();
    }

    @GetMapping("/{id}")
    public String roomDetail(@PathVariable Long id,
                             @CurrentMember Member member,
                             @Qualifier("join") @PageableDefault(size = 20, sort = "grade", direction = Sort.Direction.ASC) Pageable joinPageable,
                             @Qualifier("competition") @PageableDefault(size = 9, sort = "createdDate", direction = Sort.Direction.ASC) Pageable competitionPageable,
                             Model model) {
        log.info("roomDetail");
        Room room = roomService.findOne(id);
        Join myJoin = joinService.findOne(member.getId(), room.getId());
        model.addAttribute("room", room);
        model.addAttribute("myJoin", myJoin);

        Page<Join> joins = joinService.findByRoomId(room.getId(), joinPageable);
        model.addAttribute("joins", joins);
        log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
                joins.getTotalElements(), joins.getTotalPages(), joins.getSize(),
                joins.getNumber(), joins.getNumberOfElements());

        Page<Competition> competitions = competitionService.findCompetitions(room.getId(), competitionPageable);
        model.addAttribute("competitions", competitions);
        log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
                competitions.getTotalElements(), competitions.getTotalPages(), competitions.getSize(),
                competitions.getNumber(), competitions.getNumberOfElements());
        return "room/detail";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, @CurrentMember Member member,
                         RedirectAttributes attributes) {
        log.info("delete room");
        roomService.deleteRoom(id);
        attributes.addFlashAttribute("message", "방을 삭제했습니다.");
        return "redirect:/";
    }

    @GetMapping("/{id}/update")
    public String updateForm(@PathVariable Long id, Model model, @CurrentMember Member member) {
        Room room = roomService.findOne(id);
        model.addAttribute("roomForm", new UpdateRoomForm(room.getId(), room.getName(), room.isAccess()));
        return "room/updateRoomForm";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute("roomForm") @Valid UpdateRoomForm roomForm,
                         BindingResult result, @CurrentMember Member member,
                         RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "room/updateRoomForm";
        }
        log.info("room = {}", roomForm);
        roomService.updateRoom(roomForm.getId(), roomForm.getName(), roomForm.isAccess());
        attributes.addFlashAttribute("message", "방 정보를 수정했습니다.");
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{id}/invite")
    public String inviteForm(@PathVariable Long id,
                             @CurrentMember Member member,
                             Model model) {

        Room room = roomService.findOne(id);
        InviteForm inviteForm = new InviteForm();
        log.info("inviteForm = {}", inviteForm);
        model.addAttribute("room", room);
        model.addAttribute("inviteForm", inviteForm);
        return "room/inviteMemberForm";
    }

    @PostMapping("/{roomId}/invite")
    public String invite(@PathVariable("roomId") Room room,
                         @Valid InviteForm inviteForm,
                         BindingResult result,
                         @CurrentMember Member member,
                         RedirectAttributes attributes) {

        log.info("inviteForm={}", inviteForm);
        List<Join> joins = joinService.findByRoomId(room.getId());
        Join join = joins.stream().filter(b -> b.getMember().getId().equals(member.getId()))
                .findFirst().orElse(null);

        List<String> emails = inviteForm.getEmails();

        Set<Member> members = memberService.findMembers(emails);
        for (int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            Member findMember = members.stream().filter(m -> m.getEmail().equals(email)).findAny().orElse(null);
            if(findMember == null) {
                result.addError(new FieldError("inviteForm", "emails[" + i + "]", email,false, null, null, "존재하지 않는 회원입니다."));
                continue;
            }
            Join findJoin = joins.stream().filter(b -> b.getMember().getEmail().equals(email)).findAny().orElse(null);
            if (findJoin != null) {
                result.addError(new FieldError("inviteForm", "emails[" + i + "]", email,false,null, null, "이미 참여중인 회원입니다."));
            }
//            Alarm alarm = alarmService.findOne2(findMember.getId(), roomId);
//            if (alarm != null && alarm.getStatus().equals(AlarmStatus.BEFORE)) {
//                result.addError(new FieldError("inviteForm", "emails[" + i + "]", email,false,null, null, "이미 초대 요청한 회원입니다."));
//            }
        }

        if (result.hasErrors()) {
            log.info("유효성 검사 오류!");
            return "room/inviteMemberForm";
        }

        if (emails.size() != inviteForm.getCount()) {
            result.addError(new ObjectError("inviteForm", null, null, "오류가 발생했습니다."));
            return "room/inviteMemberForm";
        }

        log.info("초대");
        roomService.inviteRoom(room, members, member.getName());
        attributes.addFlashAttribute("message", "회원들에게 방 초대 요청을 보냈습니다.");
        return "redirect:/rooms/{roomId}";
    }

    @Getter @Setter @ToString
    @NoArgsConstructor @AllArgsConstructor
    static class UpdateRoomForm {

        @NotNull
        private Long id;

        @NotBlank(message = "방명을 입력하세요.")
        private String name;

        @NotNull(message = "공개 여부를 선택하세요.")
        private boolean access;
    }

    @Getter @Setter @ToString
    private static class InviteForm {

        @Min(value = 1, message = "최소 1명부터 초대 가능합니다.")
        @Max(value = 100, message = "최대 100명까지 초대 가능합니다.")
        private Long count;

        @NotNull(message = "null 값일 수 없습니다.")
        private List<@NotBlank(message = "회원 아이디를 입력하세요.") String> emails = new ArrayList<>();

        public InviteForm() {
            this.count = 1L;
            this.emails.add("");
        }
    }
}
