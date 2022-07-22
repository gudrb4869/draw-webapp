package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomSetting;
import hongik.ce.jolup.dto.*;
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
import java.util.stream.Collectors;

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
        List<BelongDto> belongs = belongService.findByMemberId(member.getId());
        model.addAttribute("belongs", belongs);
        return "room/list";
    }

    @GetMapping("/create")
    public String createRoom(Model model) {
        model.addAttribute("roomForm", new RoomForm());
        return "room/create";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute @Valid RoomForm roomForm,
                             BindingResult result,
                             @AuthenticationPrincipal Member member,
                             Model model) {
        if (result.hasErrors()) {
            return "room/create";
        }
        log.info("roomForm = {}", roomForm);

        Room room = Room.builder().title(roomForm.getTitle()).roomSetting(roomForm.getRoomSetting()).build();

        Long roomId = roomService.saveRoom(room);
        BelongDto belongDto = BelongDto.builder().memberDto(member.toDto()).
                roomDto(roomService.findOne(roomId)).belongType(BelongType.MASTER).build();
        belongService.save(member.getId(), roomId, BelongType.MASTER);
        return "redirect:/rooms";
    }

    @GetMapping("/{roomId}")
    public String roomDetail(@PathVariable Long roomId, Model model, @AuthenticationPrincipal Member member) {
        log.info("roomDetail");
        BelongDto belongDto = belongService.findOne(member.getId(), roomId);
        RoomDto roomDto = belongDto.getRoomDto();

        if (belongDto == null && roomDto.getRoomSetting().equals(RoomSetting.PRIVATE)) {
            log.info("존재하지 않는 방이거나 비공개 방이고 회원이 아님");
            return "error";
        }

        log.info("belongDtos");
        List<BelongDto> belongDtos = belongService.findByRoomId(roomId);

        log.info("competitionDtos");
        List<CompetitionDto> competitionDtos = competitionService.getCompetitions(roomId);

        model.addAttribute("myBelongDto", belongDto);
        model.addAttribute("roomDto", roomDto);
        model.addAttribute("belongDtos", belongDtos);
        model.addAttribute("competitionDtos", competitionDtos);
        return "room/detail";
    }

    @DeleteMapping("/{roomId}")
    public String deleteRoom(@PathVariable Long roomId, @AuthenticationPrincipal Member member) {
        log.info("delete room");
        BelongDto belongDto = belongService.findOne(member.getId(), roomId);
        if (belongDto == null || !belongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        log.info("delete room");
        roomService.deleteRoom(roomId);
        return "redirect:/rooms";
    }

    @GetMapping("/{roomId}/edit")
    public String editRoom (@PathVariable Long roomId, Model model, @AuthenticationPrincipal Member member) {
        BelongDto belongDto = belongService.findOne(member.getId(), roomId);
        RoomDto roomDto = belongDto.getRoomDto();
        if (belongDto == null || !belongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }
        model.addAttribute("roomForm", new RoomForm(roomDto.getId(), roomDto.getTitle(), roomDto.getRoomSetting()));
        return "room/edit";
    }

    @PostMapping("/{roomId}/edit")
    public String edit(@PathVariable Long roomId,
                       @ModelAttribute @Valid RoomForm roomForm,
                       BindingResult result,
                       @AuthenticationPrincipal Member member) {
        BelongDto belongDto = belongService.findOne(member.getId(), roomId);
        if (belongDto == null || !belongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        if (result.hasErrors()) {
            return "room/edit";
        }
        log.info("roomDto = {}", roomForm);
        roomService.updateRoom(roomForm.getId(), roomForm.getTitle(), roomForm.getRoomSetting());
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{roomId}/invite")
    public String inviteMember(@PathVariable Long roomId,
                               @RequestParam(name = "count", defaultValue = "1") Long count,
                               @RequestParam(name = "emails", defaultValue = "") List<String> emails,
                               @AuthenticationPrincipal Member member,
                               Model model) {

        BelongDto belongDto = belongService.findOne(member.getId(), roomId);
        if (belongDto == null || belongDto.getBelongType().equals(BelongType.USER)) {
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

        RoomDto roomDto = roomService.findOne(roomId);
        if (roomDto == null) {
            return "error";
        }
        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);

        if (myBelongDto == null || myBelongDto.getBelongType().equals(BelongType.USER)) {
            log.info("비공개 방, 회원이 아님");
            return "error";
        }

        List<String> collect = roomService.getBelongs(roomId).stream().map(BelongDto::getMemberDto).collect(Collectors.toList())
                .stream().map(MemberDto::getEmail).collect(Collectors.toList());
        List<String> emails = inviteForm.getEmails();
        for (int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            if(memberService.findOne(email) == null) {
                result.addError(new FieldError("inviteForm", "emails[" + i + "]", email,false, null, null, "존재하지 않는 회원입니다."));
            } else if (collect.contains(email)) {
                result.addError(new FieldError("inviteForm", "emails[" + i + "]", email,false,null, null, "이미 참여중인 회원입니다."));
            }
        }

        if (emails.size() != inviteForm.getCount()) {
            result.addError(new ObjectError("inviteForm", null, null, "오류가 발생했습니다."));
        }

        if (result.hasErrors()) {
            return "/room/invite";
        }

        log.info("초대");
        belongService.saveMembers(roomId, BelongType.USER, emails);

        return "redirect:/rooms/{roomId}";
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    static class RoomForm {

        @NotNull
        private Long id;

        @NotBlank(message = "방 이름을 입력해주세요!")
        @Size(min = 3, max = 30, message = "최소 3글자 최대 30글자로 입력해주세요!")
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
