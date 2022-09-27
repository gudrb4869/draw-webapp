package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.join.Grade;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.Access;
import hongik.ce.jolup.service.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
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

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("roomForm", new CreateRoomForm());
        return "room/createRoomForm";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("roomForm") @Valid CreateRoomForm roomForm,
                         BindingResult result,
                         @AuthenticationPrincipal Member member) {

        log.info("roomForm = {}", roomForm);
        if (result.hasErrors()) {
            return "room/createRoomForm";
        }

        Room room = Room.builder().name(roomForm.getName()).access(roomForm.getAccess()).master(member).build();
        Long roomId = roomService.saveRoom(room);
        joinService.save(member.getId(), roomId, Grade.ADMIN);
        return "redirect:/rooms/" + roomId;
    }

    @GetMapping("/{roomId}")
    public String roomDetail(@PathVariable("roomId") Room room,
                             @AuthenticationPrincipal Member member,
                             @Qualifier("join") @PageableDefault Pageable joinPageable,
                             @Qualifier("competition") @PageableDefault Pageable competitionPageable,
                             Model model) {
        log.info("roomDetail");

        Join myJoin = joinService.findOne(member.getId(), room.getId());
        if (myJoin == null && room.getAccess().equals(Access.PRIVATE)) {
            log.info("비공개 방이고 회원이 아님");
            return "error";
        }
        model.addAttribute("room", room);
        model.addAttribute("myJoin", myJoin);

        Page<Join> joins = joinService.findByRoomId(room.getId(), joinPageable);
        model.addAttribute("joins", joins);
        log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
                joins.getTotalElements(), joins.getTotalPages(), joins.getSize(),
                joins.getNumber(), joins.getNumberOfElements());

        Page<Competition> competitions = competitionService.findCompetitions(room.getId(), competitionPageable);
//        Page<Competition> competitions = competitionService.findCompetitions(roomId, competitionPageable);
        model.addAttribute("competitions", competitions);


        log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
                competitions.getTotalElements(), competitions.getTotalPages(), competitions.getSize(),
                competitions.getNumber(), competitions.getNumberOfElements());
        /*log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
                competitions.getTotalElements(), competitions.getTotalPages(), competitions.getSize(),
                competitions.getNumber(), competitions.getNumberOfElements());*/
        return "room/detail";
    }

    @DeleteMapping("/{roomId}")
    public String delete(@PathVariable Long roomId, @AuthenticationPrincipal Member member) {
        log.info("delete room");
        Join join = joinService.findOne(member.getId(), roomId);
        if (join == null || !join.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        log.info("delete room");
        roomService.deleteRoom(roomId);
        return "redirect:/";
    }

    @GetMapping("/{roomId}/update")
    public String updateForm(@PathVariable Long roomId, Model model, @AuthenticationPrincipal Member member) {
        Join join = joinService.findOne(member.getId(), roomId);
        if (join == null || !join.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        Room room = join.getRoom();
        model.addAttribute("roomForm", new UpdateRoomForm(room.getId(), room.getName(), room.getAccess()));
        return "room/updateRoomForm";
    }

    @PostMapping("/{roomId}/update")
    public String update(@PathVariable Long roomId,
                         @ModelAttribute("roomForm") @Valid UpdateRoomForm roomForm,
                         BindingResult result,
                         @AuthenticationPrincipal Member member) {
        Join join = joinService.findOne(member.getId(), roomId);
        if (join == null || !join.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        if (result.hasErrors()) {
            return "room/updateRoomForm";
        }
        log.info("room = {}", roomForm);
        roomService.updateRoom(roomForm.getId(), roomForm.getName(), roomForm.getAccess());
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{roomId}/invite")
    public String inviteForm(@PathVariable Long roomId,
                             @AuthenticationPrincipal Member member,
                             Model model) {

        Join join = joinService.findOne(member.getId(), roomId);
        if (join == null || join.getGrade().equals(Grade.USER)) {
            return "error";
        }

        InviteForm inviteForm = new InviteForm();
        log.info("inviteForm = {}", inviteForm);
        model.addAttribute("roomId", roomId);
        model.addAttribute("inviteForm", inviteForm);
        return "room/inviteMemberForm";
    }

    @PostMapping("/{roomId}/invite")
    public String invite(@PathVariable("roomId") Room room,
                         @Valid InviteForm inviteForm,
                         BindingResult result,
                         @AuthenticationPrincipal Member member) {

        log.info("inviteForm={}", inviteForm);
        List<Join> joins = joinService.findByRoomId(room.getId());
        Join join = joins.stream().filter(b -> b.getMember().getId().equals(member.getId()))
                .findFirst().orElse(null);
        if (join == null || join.getGrade().equals(Grade.USER)) {
            log.info("방의 회원이 아니거나, 방장 또는 매니저가 아니라 초대 불가능!");
            return "error";
        }

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

        return "redirect:/rooms/{roomId}";
    }

    @Getter @Setter @ToString
    @NoArgsConstructor @AllArgsConstructor
    static class CreateRoomForm {

        @NotBlank(message = "방명을 입력하세요.")
        private String name;

        @NotNull(message = "공개 여부를 선택하세요.")
        private Access access;
    }

    @Getter @Setter @ToString
    @NoArgsConstructor @AllArgsConstructor
    static class UpdateRoomForm {

        @NotNull
        private Long id;

        @NotBlank(message = "방명을 입력하세요.")
        private String name;

        @NotNull(message = "공개 여부를 선택하세요.")
        private Access access;
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
