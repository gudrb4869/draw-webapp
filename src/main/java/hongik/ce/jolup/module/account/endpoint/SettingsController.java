package hongik.ce.jolup.module.account.endpoint;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.endpoint.form.NameForm;
import hongik.ce.jolup.module.account.endpoint.form.NotificationForm;
import hongik.ce.jolup.module.account.endpoint.form.PasswordForm;
import hongik.ce.jolup.module.account.endpoint.form.Profile;
import hongik.ce.jolup.module.account.application.AccountService;
import hongik.ce.jolup.module.account.endpoint.validator.NameFormValidator;
import hongik.ce.jolup.module.account.endpoint.validator.PasswordFormValidator;
import hongik.ce.jolup.module.account.endpoint.validator.ProfileValidator;
import hongik.ce.jolup.module.account.support.CurrentUser;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SettingsController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/" + SETTINGS_PROFILE_VIEW_NAME;
    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/" + SETTINGS_PASSWORD_VIEW_NAME;
    static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
    static final String SETTINGS_ACCOUNT_URL = "/" + SETTINGS_ACCOUNT_VIEW_NAME;
    static final String SETTINGS_NOTIFICATION_VIEW_NAME = "settings/notification";
    static final String SETTINGS_NOTIFICATION_URL = "/" + SETTINGS_NOTIFICATION_VIEW_NAME;

    private final AccountService accountService;
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
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("profile", Profile.from(account));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@Valid Profile profile, Errors errors, @CurrentUser Account account,
                                   Model model, RedirectAttributes attributes) throws IOException {
        profileValidator.validatePassword(profile, account, errors);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }
        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필을 수정하였습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String passwordUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(@Valid PasswordForm passwordForm, Errors errors, @CurrentUser Account account,
                                 Model model, RedirectAttributes attributes) {
        passwordFormValidator.validateForm(passwordForm, account, errors);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }
        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "비밀번호를 변경했습니다.");
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }

    @GetMapping(SETTINGS_NOTIFICATION_URL)
    public String notificationForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(NotificationForm.from(account));
        return SETTINGS_NOTIFICATION_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATION_URL)
    public String updateNotification(@CurrentUser Account account, @Valid NotificationForm notificationForm, BindingResult bindingResult, Model model, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_NOTIFICATION_VIEW_NAME;
        }
        accountService.updateNotification(account, notificationForm);
        attributes.addFlashAttribute("message", "알림설정을 수정하였습니다.");
        return "redirect:" + SETTINGS_NOTIFICATION_URL;
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String nameForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new NameForm(account.getName()));
        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String updateName(@CurrentUser Account account, @Valid NameForm nameForm, Errors errors,
                             Model model, RedirectAttributes attributes) {
        nameFormValidator.validatePassword(nameForm, account, errors);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }
        accountService.updateName(account, nameForm.getName());
        attributes.addFlashAttribute("message", "이름을 수정하였습니다.");
        return "redirect:" + SETTINGS_ACCOUNT_URL;
    }

    @DeleteMapping(SETTINGS_ACCOUNT_URL)
    public String deleteMember(@CurrentUser Account account,
                               Model model, RedirectAttributes attributes) {
        accountService.remove(account);
        attributes.addFlashAttribute("message", "회원탈퇴에 성공하였습니다.");
        return "redirect:/";
    }

}
