package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.dto.BelongDto;
import hongik.ce.jolup.service.BelongService;
import hongik.ce.jolup.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null || !myBelongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        BelongDto belongDto = belongService.findByIdAndRoomId(belongId, roomId);
        if (belongDto == null) {
            return "error";
        }

        model.addAttribute("belongDto", belongDto);
        return "belong/edit";
    }

    @PutMapping("/{belongId}")
    public String edit(@PathVariable Long roomId, @PathVariable Long belongId,
                       @AuthenticationPrincipal Member member,
                       @ModelAttribute @Valid BelongDto belongDto, BindingResult result) {

        log.info("PUT : edit Belong, belongDto = {}", belongDto);
        if (result.hasErrors()) {
            return "belong/edit";
        }

        BelongDto myBelongDto = belongService.findOne(member.getId(), roomId);
        if (myBelongDto == null || !myBelongDto.getBelongType().equals(BelongType.MASTER)) {
            return "error";
        }

        if (belongService.findByIdAndRoomId(belongId, roomId) == null) {
            return "error";
        }

        belongService.saveBelong(belongDto);

        return "redirect:/rooms/{roomId}";
    }
}
