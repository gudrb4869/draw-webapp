package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.dto.BelongDto;
import hongik.ce.jolup.dto.RoomDto;
import hongik.ce.jolup.service.BelongService;
import hongik.ce.jolup.service.MemberService;
import hongik.ce.jolup.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

        BelongDto belongDto = BelongDto.builder().memberDto(member.toDto()).roomDto(roomService.findOne(roomId)).build();
        belongService.saveBelong(belongDto);
        return "redirect:/room";
    }

    @GetMapping("/{roomId}")
    public String roomDetail(@PathVariable Long roomId, Model model) {
        RoomDto roomDto = roomService.findOne(roomId);
        List<BelongDto> belongDtos = roomService.getBelongs(roomId);
        if (roomDto == null) {
            return "error";
        }
        model.addAttribute("roomDto", roomDto);
        model.addAttribute("belongDtos", belongDtos);
        return "room/detail";
    }

    @DeleteMapping("/{roomId}")
    public String deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return "redirect:/room/";
    }
}
