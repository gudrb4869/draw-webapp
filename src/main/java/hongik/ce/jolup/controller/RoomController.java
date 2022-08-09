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
import org.springframework.data.domain.Page;
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
import java.util.Optional;

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

    @GetMapping
    public String roomList(Model model, @AuthenticationPrincipal Member member,
                           @RequestParam(required = false, defaultValue = "1", value = "page") int page) {
        log.info("room_list");
        if (page <= 0) {
            return "error";
        }
        Page<Belong> belongPage = belongService.findByMemberId(member.getId(), page - 1);
        int totalPage = belongPage.getTotalPages();
        model.addAttribute("belongs", belongPage.getContent());
        model.addAttribute("totalPage", totalPage);
        log.info("totalPage = {}", totalPage);
        return "rooms/list";
    }

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

        Room room = Room.builder().title(roomForm.getTitle()).roomSetting(roomForm.getRoomSetting()).build();

        Long roomId = roomService.saveRoom(room);
        
        belongService.save(member.getId(), roomId, BelongType.ADMIN);
        return "redirect:/rooms";
    }

    @GetMapping("/{roomId}")
    public String roomDetail(@PathVariable Long roomId, Model model, @AuthenticationPrincipal Member member,
                             @RequestParam(required = false, defaultValue = "1", value = "belongPage") int belongPage,
                             @RequestParam(required = false, defaultValue = "1", value = "competitionPage") int competitionPage) {
        log.info("roomDetail");

        Room room = roomService.findOne(roomId);
        if (room == null) {
            log.info("존재하지 않는 방임");
            return "error";
        }

        Belong belong = belongService.findOne(member.getId(), roomId);
        if (belong == null && room.getRoomSetting().equals(RoomSetting.PRIVATE)) {
            log.info("비공개 방이고 회원이 아님");
            return "error";
        }
        Page<Belong> belongs = belongService.findByRoomId(roomId, belongPage - 1);
        int totalBelongPage = belongs.getTotalPages();
        Page<Competition> competitions = competitionService.findCompetitions(roomId ,competitionPage - 1);
        int totalCompetitionPage = competitions.getTotalPages();

        model.addAttribute("myBelong", belong);
        model.addAttribute("room", room);
        model.addAttribute("belongs", belongs.getContent());
        model.addAttribute("totalBelongPage", totalBelongPage);
        model.addAttribute("competitions", competitions.getContent());
        model.addAttribute("totalCompetitionPage", totalCompetitionPage);
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
        return "redirect:/rooms";
    }

    @GetMapping("/{roomId}/edit")
    public String updateForm(@PathVariable Long roomId, Model model, @AuthenticationPrincipal Member member) {
        Belong belong = belongService.findOne(member.getId(), roomId);
        if (belong == null || !belong.getBelongType().equals(BelongType.ADMIN)) {
            return "error";
        }

        Room room = belong.getRoom();
        model.addAttribute("roomForm", new UpdateRoomForm(room.getId(), room.getTitle(), room.getRoomSetting()));
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
        roomService.updateRoom(roomForm.getId(), roomForm.getTitle(), roomForm.getRoomSetting());
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{roomId}/invite")
    public String inviteForm(@PathVariable Long roomId,
                             @RequestParam(name = "count", defaultValue = "1") Long count,
                             @RequestParam(name = "emails", defaultValue = "") List<String> emails,
                             @AuthenticationPrincipal Member member,
                             Model model) {

        Belong belong = belongService.findOne(member.getId(), roomId);
        if (belong == null || belong.getBelongType().equals(BelongType.USER)) {
            return "error";
        }

        InviteForm inviteForm = new InviteForm();
        inviteForm.setCount(count);
        for (int i = 0; i < count; i++) {
            if (i < emails.size())
                inviteForm.addEmail(emails.get(i));
            else
                inviteForm.addEmail("");
        }
        log.info("inviteForm = {}", inviteForm);
        model.addAttribute("roomId", roomId);
        model.addAttribute("inviteForm", inviteForm);
        return "rooms/invite";
    }

    @PostMapping("/{roomId}/invite")
    public String invite(@PathVariable Long roomId,
                         @Valid InviteForm inviteForm,
                         BindingResult result,
                         @AuthenticationPrincipal Member member,
                         Model model) {

        List<Belong> belongs = belongService.findByRoomId(roomId);
        Belong belong = belongs.stream().filter(b -> b.getMember().getId().equals(member.getId()))
                .findFirst().orElse(null);
        if (belong == null || belong.getBelongType().equals(BelongType.USER)) {
            log.info("방의 회원이 아니거나, 방장 또는 매니저가 아니라 초대 불가능!");
            return "error";
        }

        List<String> emails = inviteForm.getEmails();
        if (emails.size() != inviteForm.getCount()) {
            result.addError(new ObjectError("inviteForm", null, null, "오류가 발생했습니다."));
        }

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

        @NotBlank(message = "방 이름을 입력해주세요!")
        private String title;

        @NotNull(message = "방 공개 여부를 선택해주세요!")
        private RoomSetting roomSetting;
    }

    @Getter @Setter @ToString
    @NoArgsConstructor @AllArgsConstructor
    static class UpdateRoomForm {

        @NotNull
        private Long id;

        @NotBlank(message = "방 이름을 입력해주세요!")
        private String title;

        @NotNull(message = "방 공개 여부를 선택해주세요!")
        private RoomSetting roomSetting;
    }

    @Getter @Setter @NoArgsConstructor
    @ToString @AllArgsConstructor @Builder
    private static class InviteForm {

        @NotNull(message = "초대 인원 수는 필수 입력 값입니다.")
        @Min(value = 1, message = "최소 1명에서 최대 100명까지 초대 가능합니다.")
        @Max(value = 100, message = "최소 1명에서 최대 100명까지 초대 가능합니다.")
        private Long count;

        @NotNull(message = "널 오류")
        @Size(min = 1, max = 100, message = "리스트 크기 오류")
        private List<@NotBlank(message = "회원 아이디는 필수 입력 값입니다.") String> emails = new ArrayList<>();

        public void addEmail(String email) {
            this.emails.add(email);
        }
    }
}
