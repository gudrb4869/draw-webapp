package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomSetting;
import hongik.ce.jolup.service.BelongService;
import hongik.ce.jolup.service.CompetitionService;
import hongik.ce.jolup.service.MemberService;
import hongik.ce.jolup.service.RoomService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final MemberService memberService;
    private final BelongService belongService;
    private final RoomService roomService;
    private final CompetitionService competitionService;

    @GetMapping
    public String rooms(Model model, @AuthenticationPrincipal Member member) {
        log.info("room_list");
        List<Belong> belongs = belongService.findByMemberId(member.getId());
        model.addAttribute("belongs", belongs);
        return "room/list";
    }

    @GetMapping("/create")
    public String createRoom(Model model) {
        model.addAttribute("roomForm", new CreateRoomForm());
        return "room/create";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute("roomForm") @Valid CreateRoomForm roomForm,
                             BindingResult result,
                             @AuthenticationPrincipal Member member) {

        log.info("roomForm = {}", roomForm);

        if (result.hasErrors()) {
            return "room/create";
        }

        Room room = Room.builder().title(roomForm.getTitle()).roomSetting(roomForm.getRoomSetting()).build();

        Long roomId = roomService.saveRoom(room);
        
        belongService.save(member.getId(), roomId, BelongType.MASTER);
        return "redirect:/rooms";
    }

    @GetMapping("/{roomId}")
    public String roomDetail(@PathVariable Long roomId, Model model, @AuthenticationPrincipal Member member) {
        log.info("roomDetail");

        List<Belong> belongs = belongService.findByRoomId(roomId);
        if (belongs.isEmpty()) {
            log.info("존재하지 않는 방임");
            return "error";
        }
        Belong belong = belongs.stream()
                .filter(b -> b.getMember().getId().equals(member.getId())).findAny().orElse(null);
        Room room = belongs.get(0).getRoom();
        if (belong == null && room.getRoomSetting().equals(RoomSetting.PRIVATE)) {
            log.info("비공개 방이고 회원이 아님");
            return "error";
        }

        List<Competition> competitions = competitionService.findCompetitions(roomId);
        model.addAttribute("myBelong", belong);
        model.addAttribute("room", room);
        model.addAttribute("belongs", belongs);
        model.addAttribute("competitions", competitions);
        return "room/detail";
    }

    @DeleteMapping("/{roomId}")
    public String deleteRoom(@PathVariable Long roomId, @AuthenticationPrincipal Member member) {
        log.info("delete room");
        Belong belong = belongService.findOne(member.getId(), roomId);
        if (belong == null || !belong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        log.info("delete room");
        roomService.deleteRoom(roomId);
        return "redirect:/rooms";
    }

    @GetMapping("/{roomId}/edit")
    public String editRoom (@PathVariable Long roomId, Model model, @AuthenticationPrincipal Member member) {
        Belong belong = belongService.findOne(member.getId(), roomId);
        if (belong == null || !belong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        Room room = belong.getRoom();
        model.addAttribute("roomForm", new UpdateRoomForm(room.getId(), room.getTitle(), room.getRoomSetting()));
        return "room/edit";
    }

    @PostMapping("/{roomId}/edit")
    public String edit(@PathVariable Long roomId,
                       @ModelAttribute("roomForm") @Valid UpdateRoomForm roomForm,
                       BindingResult result,
                       @AuthenticationPrincipal Member member) {
        Belong belong = belongService.findOne(member.getId(), roomId);
        if (belong == null || !belong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        if (result.hasErrors()) {
            return "room/edit";
        }
        log.info("room = {}", roomForm);
        roomService.updateRoom(roomForm.getId(), roomForm.getTitle(), roomForm.getRoomSetting());
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{roomId}/invite")
    public String inviteMember(@PathVariable Long roomId,
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
                inviteForm.addEmail(new String());
        }
        log.info("inviteForm = {}", inviteForm);
        model.addAttribute("roomId", roomId);
        model.addAttribute("inviteForm", inviteForm);
        return "room/invite";
    }

    @PostMapping("/{roomId}/invite")
    public String inviteMember(@PathVariable Long roomId,
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
            Optional<Member> findMember = members.stream().filter(m -> m.getEmail().equals(email)).findAny();
            if(findMember.isEmpty()) {
                result.addError(new FieldError("inviteForm", "emails[" + i + "]", email,false, null, null, "존재하지 않는 회원입니다."));
                continue;
            }
            Optional<Belong> optionalBelong = belongs.stream().filter(b -> b.getMember().getEmail().equals(email)).findAny();
            if (optionalBelong.isPresent()) {
                result.addError(new FieldError("inviteForm", "emails[" + i + "]", email,false,null, null, "이미 참여중인 회원입니다."));
            }
        }

        if (result.hasErrors()) {
            return "/room/invite";
        }

        log.info("초대");
        belongService.saveBelongs(roomId, BelongType.USER, members);

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
