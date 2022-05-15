package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.RoomSetting;
import hongik.ce.jolup.dto.BelongDto;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.MemberDto;
import hongik.ce.jolup.dto.RoomDto;
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
@RequestMapping("/room")
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
        model.addAttribute("roomForm", new RoomDto());
        return "room/create";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute("roomForm") @Valid RoomDto roomDto,
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
        return "redirect:/room";
    }

    @GetMapping("/{roomId}")
    public String roomDetail(@PathVariable Long roomId, Model model, @AuthenticationPrincipal Member member) {
        log.info("roomDetail");
        RoomDto roomDto = roomService.findOne(roomId);

        if (roomDto == null) {
            return "error";
        }

        List<BelongDto> belongDtos = roomService.getBelongs(roomId);

        if (roomDto.getRoomSetting().equals(RoomSetting.PRIVATE) &&
                !belongDtos.stream().map(BelongDto::getMemberDto).collect(Collectors.toList())
                        .stream().map(MemberDto::getId).collect(Collectors.toList()).contains(member.getId()))
            return "error";

        List<JoinDto> joinDtos = memberService.getJoins(member.getId());

        model.addAttribute("memberBelongDto", belongService.findOne(member.getId(), roomId));
        model.addAttribute("roomDto", roomDto);
        model.addAttribute("belongDtos", belongDtos);
        model.addAttribute("joinDtos", joinDtos);
        return "room/detail";
    }

    @DeleteMapping("/{roomId}")
    public String deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return "redirect:/room/";
    }

    @GetMapping("/{roomId}/invite")
    public String inviteMember(@PathVariable Long roomId,
                               @RequestParam(name = "count", defaultValue = "1") Long count,
                               @RequestParam(name = "emails", defaultValue = "") List<String> emails,
                               Model model) {
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
                               Model model) {

        RoomDto roomDto = roomService.findOne(roomId);

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

        if (result.hasErrors()) {
            return "/room/invite";
        }

        // 초대
        for (String email : emails) {
            belongService.saveBelong(BelongDto.builder().
                    memberDto(memberService.findOne(email)).roomDto(roomDto).belongType(BelongType.USER).build());
        }

        return "redirect:/room/{roomId}";
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
