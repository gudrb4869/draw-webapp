package hongik.ce.jolup.module.member.endpoint;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.endpoint.form.NameForm;
import hongik.ce.jolup.module.member.endpoint.form.PasswordForm;
import hongik.ce.jolup.module.member.endpoint.form.Profile;
import hongik.ce.jolup.module.member.application.MemberService;
import hongik.ce.jolup.module.member.endpoint.validator.NameFormValidator;
import hongik.ce.jolup.module.member.endpoint.validator.PasswordFormValidator;
import hongik.ce.jolup.module.member.endpoint.validator.ProfileValidator;
import hongik.ce.jolup.module.member.support.CurrentMember;
import lombok.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/" + SETTINGS_PROFILE_VIEW_NAME;
    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/" + SETTINGS_PASSWORD_VIEW_NAME;
    static final String SETTINGS_MEMBER_VIEW_NAME = "settings/member";
    static final String SETTINGS_MEMBER_URL = "/" + SETTINGS_MEMBER_VIEW_NAME;
    static final String SETTINGS_NOTIFICATION_VIEW_NAME = "settings/notification";
    static final String SETTINGS_NOTIFICATION_URL = "/" + SETTINGS_NOTIFICATION_VIEW_NAME;

    private final MemberService memberService;
    private final PasswordFormValidator passwordFormValidator;
    private final ProfileValidator profileValidator;
    private final NameFormValidator nameFormValidator;

    @InitBinder("profile")
    public void profileValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(profileValidator);
    }

    @InitBinder("nameForm")
    public void nameFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nameFormValidator);
    }

    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentMember Member member, Model model) {
        model.addAttribute(member);
        model.addAttribute("profile", Profile.from(member));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@Valid Profile profile, Errors errors, @CurrentMember Member member,
                                   Model model, RedirectAttributes attributes) {
        profileValidator.validatePassword(profile, member, errors);
        if (errors.hasErrors()) {
            model.addAttribute(member);
            return SETTINGS_PROFILE_VIEW_NAME;
        }
        memberService.updateProfile(member, profile);
        attributes.addFlashAttribute("message", "프로필을 수정하였습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String passwordUpdateForm(@CurrentMember Member member, Model model) {
        model.addAttribute(member);
        model.addAttribute(new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(@Valid PasswordForm passwordForm, Errors errors, @CurrentMember Member member,
                                 Model model, RedirectAttributes attributes) {
        passwordFormValidator.validateForm(passwordForm, member, errors);
        if (errors.hasErrors()) {
            model.addAttribute("member");
            return SETTINGS_PASSWORD_VIEW_NAME;
        }
        memberService.updatePassword(member, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "비밀번호를 변경했습니다.");
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }

    @GetMapping(SETTINGS_MEMBER_URL)
    public String MemberInfoForm(@CurrentMember Member member, Model model) {
        model.addAttribute(member);
        model.addAttribute(new NameForm(member.getName()));
        return SETTINGS_MEMBER_VIEW_NAME;
    }

    @PostMapping(SETTINGS_MEMBER_URL)
    public String updateName(@CurrentMember Member member, @Valid NameForm nameForm, Errors errors,
                             Model model, RedirectAttributes attributes) {
        nameFormValidator.validatePassword(nameForm, member, errors);
        if (errors.hasErrors()) {
            model.addAttribute(member);
            return SETTINGS_MEMBER_URL;
        }
        memberService.updateName(member, nameForm.getName());
        attributes.addFlashAttribute("message", "이름을 수정하였습니다.");
        return "redirect:" + SETTINGS_MEMBER_URL;
    }

    /*@DeleteMapping(SETTINGS_MEMBER_URL)
    public String deleteMember(@ModelAttribute("memberForm") @Valid DeleteMemberForm form,
                               BindingResult result, @CurrentMember Member member,
                               Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return SETTINGS_MEMBER_VIEW_NAME;
        }
        try {
            memberService.deleteMember(member.getId());
            SecurityContextHolder.clearContext();
            attributes.addFlashAttribute("message", "회원탈퇴에 성공하였습니다.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return SETTINGS_MEMBER_VIEW_NAME;
        }
        return "redirect:" + SETTINGS_MEMBER_URL;
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
    }*/
}
