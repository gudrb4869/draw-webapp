package hongik.ce.jolup.controller;

import hongik.ce.jolup.model.SignupForm;
import hongik.ce.jolup.service.MemberService;
import hongik.ce.jolup.validator.SignupFormValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final SignupFormValidator signupFormValidator;
    private final MemberService memberService;

    @InitBinder("signupForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupFormValidator);
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        model.addAttribute("signupForm", new SignupForm());
        return "members/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupForm signupForm,
                         BindingResult result) {

        if (result.hasErrors()) {
            return "members/signup";
        }
        memberService.signup(signupForm);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        return "members/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
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
