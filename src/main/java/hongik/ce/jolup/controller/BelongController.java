package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.dto.BelongDto;
import hongik.ce.jolup.service.BelongService;
import hongik.ce.jolup.service.RoomService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms/{roomId}/belongs")
public class BelongController {

    private final RoomService roomService;
    private final BelongService belongService;

    @GetMapping("/{belongId}")
    public String editRole(@PathVariable Long roomId, @PathVariable Long belongId,
                           @AuthenticationPrincipal Member member, Model model) {
        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        Belong belong = belongService.findByIdAndRoomId(belongId, roomId);
        if (belong == null) {
            return "error";
        }

        model.addAttribute("belongForm", new BelongForm(belong.getId(), belong.getMember().getName(), belong.getBelongType()));
        return "belong/edit";
    }

    @PostMapping("/{belongId}")
    public String edit(@PathVariable Long roomId, @PathVariable Long belongId,
                       @AuthenticationPrincipal Member member,
                       @ModelAttribute @Valid BelongForm belongForm, BindingResult result) {

        log.info("POST : edit Belong, belongForm = {}", belongForm);
        if (result.hasErrors()) {
            return "belong/edit";
        }

        Belong myBelong = belongService.findOne(member.getId(), roomId);
        if (myBelong == null || !myBelong.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        belongService.update(belongForm.getId(), belongForm.getType());

        return "redirect:/rooms/{roomId}";
    }

    @Getter @Setter @ToString
    @NoArgsConstructor @AllArgsConstructor
    static class BelongForm {

        @NotNull
        private Long id;
        @NotBlank
        private String email;
        @NotNull(message = "회원 권한을 선택해주세요!")
        private BelongType type;
    }

}
