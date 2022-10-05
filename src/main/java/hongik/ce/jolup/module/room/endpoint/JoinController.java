package hongik.ce.jolup.module.room.endpoint;

import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Grade;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.application.JoinService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms/{roomId}/joins")
public class JoinController {

    private final JoinService joinService;

    @GetMapping("/{joinId}")
    public String updateForm(@PathVariable Long roomId, @PathVariable Long joinId,
                             @AuthenticationPrincipal Member member, Model model) {
        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        Join join = joinService.findByIdAndRoomId(joinId, roomId);
        if (join == null) {
            return "error";
        }

        model.addAttribute("joinForm",
                new UpdateGradeForm(join.getId(), join.getMember().getName(), join.getGrade()));
        return "join/updateGradeForm";
    }

    @PostMapping("/{joinId}")
    public String update(@PathVariable Long roomId, @PathVariable Long joinId,
                         @AuthenticationPrincipal Member member,
                         @ModelAttribute @Valid UpdateGradeForm joinForm, BindingResult result,
                         RedirectAttributes attributes) {

        log.info("POST : edit join, joinForm = {}", joinForm);
        if (result.hasErrors()) {
            return "join/updateGradeForm";
        }

        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !joinId.equals(joinForm.getId()) || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        joinService.update(joinForm.getId(), joinForm.getGrade());
        attributes.addFlashAttribute("message", "회원 등급을 수정했습니다.");
        return "redirect:/rooms/{roomId}";
    }

    @Getter @Setter @ToString
    @NoArgsConstructor @AllArgsConstructor
    static class UpdateGradeForm {

        @NotNull
        private Long id;
        @NotBlank
        private String email;
        @NotNull(message = "회원 권한을 선택하세요.")
        private Grade grade;
    }

}
