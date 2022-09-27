package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.model.PasswordForm;
import hongik.ce.jolup.model.Profile;
import hongik.ce.jolup.service.MemberService;
import hongik.ce.jolup.validator.PasswordFormValidator;
import hongik.ce.jolup.validator.ProfileValidator;
import lombok.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/" + SETTINGS_PROFILE_VIEW_NAME;
    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/" + SETTINGS_PASSWORD_VIEW_NAME;
    static final String SETTINGS_NOTIFICATION_VIEW_NAME = "settings/notification";
    static final String SETTINGS_NOTIFICATION_URL = "/" + SETTINGS_NOTIFICATION_VIEW_NAME;

    private final MemberService memberService;
    private final PasswordFormValidator passwordFormValidator;
    private final ProfileValidator profileValidator;

    @GetMapping("/profile")
    public String profile(/*@PathVariable String name, */@AuthenticationPrincipal Member member, Model model) {
/*        Member byName = memberService.findByName(name);
        if (byName == null) {
            throw new IllegalArgumentException(name + "에 해당하는 사용자가 없습니다.");
        }
        log.info("byName = {}", byName);
        log.info("member = {}", member);
        log.info("byName equals member = {}", byName.equals(member));
        model.addAttribute("member", byName);
        model.addAttribute("isOwner", byName.equals(member));*/
        model.addAttribute("member", member);
        return "members/profile";
    }

    @GetMapping("/settings/password")
    public String updatePasswordForm(@AuthenticationPrincipal Member member, Model model) {
        model.addAttribute(member);
        PasswordForm passwordForm = new PasswordForm();
        passwordForm.setEmail(member.getEmail());
        model.addAttribute(passwordForm);
        return "members/updatePasswordForm";
    }

    @PostMapping("/settings/password")
    public String updatePassword(@Valid @ModelAttribute PasswordForm passwordForm,
                                 BindingResult result, @AuthenticationPrincipal Member member,
                                 Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("member");
            return "members/updatePasswordForm";
        }
        passwordFormValidator.validate(passwordForm, result);
        if (result.hasErrors()) {
            model.addAttribute("member");
            return "members/updatePasswordForm";
        }
        memberService.updatePassword(member, passwordForm.getPassword_new());
        attributes.addFlashAttribute("message", "비밀번호를 변경했습니다.");
        return "redirect:/profile";
    }

    @GetMapping("/settings/profile")
    public String updateMemberInfoForm(@AuthenticationPrincipal Member member, Model model) {
        model.addAttribute(member);
        model.addAttribute("profile", Profile.from(member));
        return "members/updateMemberInfoForm";
    }

    @PostMapping("/settings/profile")
    public String updateMemberInfo(@ModelAttribute("profile") @Valid Profile profile,
                                   BindingResult result, @AuthenticationPrincipal Member member,
                                   Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute(member);
            return "members/updateMemberInfoForm";
        }
        profileValidator.validate(profile, result);
        if (result.hasErrors()) {
            model.addAttribute(member);
            return "members/updateMemberInfoForm";
        }
        memberService.updateProfile(member, profile);
        attributes.addFlashAttribute("message", "회원정보를 수정하였습니다.");
        return "redirect:/profile";
    }

    @GetMapping("/settings/leave")
    public String deleteMemberForm(@AuthenticationPrincipal Member member, Model model) {
        model.addAttribute("memberForm", new DeleteMemberForm(member.getEmail()));
        return "members/deleteMemberForm";
    }

    @PostMapping("/settings/leave")
    public String deleteMember(@ModelAttribute("memberForm") @Valid DeleteMemberForm form,
                               BindingResult result, @AuthenticationPrincipal Member member,
                               HttpSession httpSession,
                               Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "members/deleteMemberForm";
        }
        try {
            memberService.deleteMember(member.getId());
            httpSession.invalidate();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "members/deleteMemberForm";
        }
        return "redirect:/";
    }

    @Getter @Setter @Builder @ToString
    @NoArgsConstructor @AllArgsConstructor
    private static class DeleteMemberForm {
        @NotBlank(message = "아이디는 필수 입력 값입니다!")
        private String email;

        @NotBlank(message = "현재 비밀번호를 입력하세요.")
        private String password_current;

        public DeleteMemberForm(String email) {
            this.email = email;
        }
    }
}
