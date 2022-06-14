package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.RoomSetting;
import hongik.ce.jolup.dto.*;
import hongik.ce.jolup.service.BelongService;
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

    @GetMapping
    public String rooms(Model model, @AuthenticationPrincipal Member member) {
        List<BelongDto> belongs = memberService.getBelongs(member.getId());
        model.addAttribute("belongs", belongs);
        return "room/list";
    }

    @GetMapping("/create")
    public String createRoom(Model model) {
        model.addAttribute("roomDto", new RoomDto());
        return "room/create";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute @Valid RoomDto roomDto,
                             BindingResult result,
                             @AuthenticationPrincipal Member member,
                             Model model) {
        if (result.hasErrors()) {
            return "room/create";
        }
        log.info("roomForm = {}", roomDto);

        Long roomId = roomService.saveRoom(roomDto);

        BelongDto belongDto = BelongDto.builder().memberDto(member.toDto()).
                roomDto(roomService.findOne(roomId)).belongType(BelongType.MASTER).build();
        belongService.saveBelong(belongDto);
        return "redirect:/rooms";
    }

    @GetMapping("/{roomId}")
    public String roomDetail(@PathVariable Long roomId, Model model, @AuthenticationPrincipal Member member) {
        log.info("roomDetail");
        RoomDto roomDto = roomService.findOne(roomId);

        if (roomDto == null) {
            return "error";
        }
        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);

        if (roomDto.getRoomSetting().equals(RoomSetting.PRIVATE) && myBelongDto == null) {
            log.info("비공개 방, 회원이 아님");
            return "error";
        }

        List<BelongDto> belongDtos = roomService.getBelongs(roomId);
        List<CompetitionDto> competitionDtos = roomService.getCompetitions(roomId);
        model.addAttribute("myBelongDto", myBelongDto);
        model.addAttribute("roomDto", roomDto);
        model.addAttribute("belongDtos", belongDtos);
        model.addAttribute("competitionDtos", competitionDtos);
        return "room/detail";
    }

    @DeleteMapping("/{roomId}")
    public String deleteRoom(@PathVariable Long roomId, @AuthenticationPrincipal Member member) {
        RoomDto roomDto = roomService.findOne(roomId);
        if (roomDto == null) {
            return "error";
        }
        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);

        if (myBelongDto == null || !myBelongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        roomService.deleteRoom(roomId);
        return "redirect:/rooms";
    }

    @GetMapping("/{roomId}/edit")
    public String editRoom (@PathVariable Long roomId, Model model, @AuthenticationPrincipal Member member) {
        RoomDto roomDto = roomService.findOne(roomId);
        if (roomDto == null) {
            return "error";
        }
        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null || !myBelongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }
        model.addAttribute("roomDto", roomDto);
        return "room/edit";
    }

    @PutMapping("/{roomId}/edit")
    public String edit(@PathVariable Long roomId,
                       @ModelAttribute @Valid RoomDto roomDto,
                       BindingResult result,
                       @AuthenticationPrincipal Member member) {
        if (roomDto == null) {
            return "error";
        }
        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null || !myBelongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        if (result.hasErrors()) {
            return "room/edit";
        }
        log.info("roomDto = {}", roomDto);
        roomService.saveRoom(roomDto);
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{roomId}/invite")
    public String inviteMember(@PathVariable Long roomId,
                               @RequestParam(name = "count", defaultValue = "1") Long count,
                               @RequestParam(name = "emails", defaultValue = "") List<String> emails,
                               @AuthenticationPrincipal Member member,
                               Model model) {

        RoomDto roomDto = roomService.findOne(roomId);
        if (roomDto == null) {
            return "error";
        }
        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);

        if (myBelongDto == null || myBelongDto.getBelongType().equals(BelongType.USER)) {
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

        // 초대
        for (String email : emails) {
            belongService.saveBelong(BelongDto.builder().
                    memberDto(memberService.findOne(email)).roomDto(roomDto).belongType(BelongType.USER).build());
        }

        return "redirect:/rooms/{roomId}";
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
