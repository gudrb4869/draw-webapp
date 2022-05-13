package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.dto.UserDto;
import hongik.ce.jolup.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signup(Model model) {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        model.addAttribute("userForm", new UserForm());
        return "signup";
    }

    @PostMapping("/signup")
    public /*ModelAndView*/ String signup(@Valid UserForm userForm, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "signup";
        }
        UserDto userDto = UserDto.builder()
                .email(userForm.getEmail())
                .password(userForm.getPassword())
                .name(userForm.getName()).build();
        try {
            userService.saveUser(userDto);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }

    @GetMapping("/edit")
    public String edit(Model model, @AuthenticationPrincipal User user) {
        UserEditForm userEditForm = new UserEditForm();
        userEditForm.setName(user.getName());
        model.addAttribute("userEditForm", userEditForm);
        return "edit";
    }

    @PostMapping("edit")
    public String edit(@ModelAttribute @Valid UserEditForm userEditForm,
                       BindingResult result, @AuthenticationPrincipal User user,
                       Model model) {
        if (result.hasErrors()) {
            return "edit";
        }
        UserDto userDto = userService.getUser(user.getId());
        log.info("userDto={}", userDto);
        log.info("userEditForm = {}", userEditForm);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(userEditForm.getPassword_current(), userDto.getPassword())) {
            model.addAttribute("errorMessage", "에러 발생");
            return "edit";
        }
        userDto.setPassword(userEditForm.getPassword_new());
        userDto.setName(userEditForm.getName());
        userService.UpdateUser(userDto);
        return "redirect:/";
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
}
