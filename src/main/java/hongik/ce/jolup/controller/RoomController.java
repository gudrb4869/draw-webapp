package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.alarm.Alarm;
import hongik.ce.jolup.domain.alarm.AlarmStatus;
import hongik.ce.jolup.domain.alarm.AlarmType;
import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomSetting;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final MemberService memberService;
    private final BelongService belongService;
    private final RoomService roomService;
    private final CompetitionService competitionService;
    private final AlarmService alarmService;

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("roomForm", new CreateRoomForm());
        return "rooms/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("roomForm") @Valid CreateRoomForm roomForm,
                         BindingResult result,
                         @AuthenticationPrincipal Member member) {

        log.info("roomForm = {}", roomForm);
        if (result.hasErrors()) {
            return "rooms/create";
        }

        Room room = Room.builder().name(roomForm.getName()).roomSetting(roomForm.getRoomSetting()).build();
        Long roomId = roomService.saveRoom(room);
        belongService.save(member.getId(), roomId, BelongType.ADMIN);
        return "redirect:/";
    }

    @GetMapping("/{roomId}")
    public String roomDetail(@PathVariable Long roomId,
                             @AuthenticationPrincipal Member member,
                             @Qualifier("belong") @PageableDefault Pageable belongPageable,
                             @Qualifier("competition") @PageableDefault Pageable competitionPageable,
                             Model model) {
        log.info("roomDetail");

        Room room = roomService.findOne(roomId);
        if (room == null) {
            log.info("존재하지 않는 방임");
            return "error";
        }

        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null && room.getRoomSetting().equals(RoomSetting.PRIVATE)) {
            log.info("비공개 방이고 회원이 아님");
            return "error";
        }
        model.addAttribute("room", room);
        model.addAttribute("myBelong", myBelong);

        Page<Belong> belongs = belongService.findByRoomId(roomId, belongPageable);
        model.addAttribute("belongs", belongs);
        log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
                belongs.getTotalElements(), belongs.getTotalPages(), belongs.getSize(),
                belongs.getNumber(), belongs.getNumberOfElements());

        Page<Competition> competitions = competitionService.findCompetitions(roomId, competitionPageable);
        model.addAttribute("competitions", competitions);
        log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
                competitions.getTotalElements(), competitions.getTotalPages(), competitions.getSize(),
                competitions.getNumber(), competitions.getNumberOfElements());
        return "rooms/detail";
    }

    @DeleteMapping("/{roomId}")
    public String delete(@PathVariable Long roomId, @AuthenticationPrincipal Member member) {
        log.info("delete room");
        Belong belong = belongService.findOne(member.getId(), roomId);
        if (belong == null || !belong.getBelongType().equals(BelongType.ADMIN)) {
            return "error";
        }

        log.info("delete room");
        roomService.deleteRoom(roomId);
        return "redirect:/";
    }

    @GetMapping("/{roomId}/edit")
    public String updateForm(@PathVariable Long roomId, Model model, @AuthenticationPrincipal Member member) {
        Belong belong = belongService.findOne(member.getId(), roomId);
        if (belong == null || !belong.getBelongType().equals(BelongType.ADMIN)) {
            return "error";
        }

        Room room = belong.getRoom();
        model.addAttribute("roomForm", new UpdateRoomForm(room.getId(), room.getName(), room.getRoomSetting()));
        return "rooms/update";
    }

    @PostMapping("/{roomId}/edit")
    public String update(@PathVariable Long roomId,
                         @ModelAttribute("roomForm") @Valid UpdateRoomForm roomForm,
                         BindingResult result,
                         @AuthenticationPrincipal Member member) {
        Belong belong = belongService.findOne(member.getId(), roomId);
        if (belong == null || !belong.getBelongType().equals(BelongType.ADMIN)) {
            return "error";
        }

        if (result.hasErrors()) {
            return "rooms/update";
        }
        log.info("room = {}", roomForm);
        roomService.updateRoom(roomForm.getId(), roomForm.getName(), roomForm.getRoomSetting());
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{roomId}/invite")
    public String inviteForm(@PathVariable Long roomId,
                             @AuthenticationPrincipal Member member,
                             Model model) {

        Belong belong = belongService.findOne(member.getId(), roomId);
        if (belong == null || belong.getBelongType().equals(BelongType.USER)) {
            return "error";
        }

        InviteForm inviteForm = new InviteForm();
        log.info("inviteForm = {}", inviteForm);
        model.addAttribute("roomId", roomId);
        model.addAttribute("inviteForm", inviteForm);
        return "rooms/invite";
    }

    @PostMapping("/{roomId}/invite")
    public String invite(@PathVariable Long roomId,
                         @Valid InviteForm inviteForm,
                         BindingResult result,
                         @AuthenticationPrincipal Member member) {

        log.info("inviteForm={}", inviteForm);
        List<Belong> belongs = belongService.findByRoomId(roomId);
        Belong belong = belongs.stream().filter(b -> b.getMember().getId().equals(member.getId()))
                .findFirst().orElse(null);
        if (belong == null || belong.getBelongType().equals(BelongType.USER)) {
            log.info("방의 회원이 아니거나, 방장 또는 매니저가 아니라 초대 불가능!");
            return "error";
        }

        List<String> emails = inviteForm.getEmails();

        List<Member> members = memberService.findMembers(emails);
        for (int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            Member findMember = members.stream().filter(m -> m.getEmail().equals(email)).findAny().orElse(null);
            if(findMember == null) {
                result.addError(new FieldError("inviteForm", "emails[" + i + "]", email,false, null, null, "존재하지 않는 회원입니다."));
                continue;
            }
            Belong findBelong = belongs.stream().filter(b -> b.getMember().getEmail().equals(email)).findAny().orElse(null);
            if (findBelong != null) {
                result.addError(new FieldError("inviteForm", "emails[" + i + "]", email,false,null, null, "이미 참여중인 회원입니다."));
            }
//            Alarm alarm = alarmService.findOne2(findMember.getId(), roomId);
//            if (alarm != null && alarm.getStatus().equals(AlarmStatus.BEFORE)) {
//                result.addError(new FieldError("inviteForm", "emails[" + i + "]", email,false,null, null, "이미 초대 요청한 회원입니다."));
//            }
        }

        if (result.hasErrors()) {
            log.info("유효성 검사 오류!");
            return "/rooms/invite";
        }

        if (emails.size() != inviteForm.getCount()) {
            result.addError(new ObjectError("inviteForm", null, null, "오류가 발생했습니다."));
            return "/rooms/invite";
        }

        log.info("초대");
        for (Member m : members) {
            Alarm alarm = Alarm.builder().sendMember(member).receiveMember(m)
                    .alarmType(AlarmType.ROOM_INVITE).status(AlarmStatus.BEFORE).requestId(roomId).build();
            alarmService.save(alarm);
        }

        return "redirect:/rooms/{roomId}";
    }

    @Getter @Setter @ToString
    @NoArgsConstructor @AllArgsConstructor
    static class CreateRoomForm {

        @NotBlank(message = "방명을 입력하세요.")
        private String name;

        @NotNull(message = "공개 여부를 선택하세요.")
        private RoomSetting roomSetting;
    }

    @Getter @Setter @ToString
    @NoArgsConstructor @AllArgsConstructor
    static class UpdateRoomForm {

        @NotNull
        private Long id;

        @NotBlank(message = "방명을 입력하세요.")
        private String name;

        @NotNull(message = "공개 여부를 선택하세요.")
        private RoomSetting roomSetting;
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
