package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.dto.MemberDto;
import hongik.ce.jolup.service.MemberService;
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
import org.springframework.web.bind.annotation.PutMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/signup")
    public String signup(Model model) {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        model.addAttribute("memberForm", new MemberForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid MemberForm memberForm, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "signup";
        }
        MemberDto memberDto = MemberDto.builder()
                .email(memberForm.getEmail())
                .password(memberForm.getPassword())
                .name(memberForm.getName()).build();
        try {
            memberService.saveMember(memberDto);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
        return "redirect:/login";
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
    public String edit(Model model, @AuthenticationPrincipal Member member) {
        MemberEditForm memberEditForm = new MemberEditForm();
        memberEditForm.setName(member.getName());
        model.addAttribute("memberEditForm", memberEditForm);
        return "edit";
    }

    @PutMapping("edit")
    public String edit(@ModelAttribute @Valid MemberEditForm memberEditForm,
                       BindingResult result, @AuthenticationPrincipal Member member,
                       Model model) {
        if (result.hasErrors()) {
            return "edit";
        }
        MemberDto memberDto = memberService.findOne(member.getId());
        log.info("memberDto={}", memberDto);
        log.info("memberEditForm = {}", memberEditForm);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(memberEditForm.getPassword_current(), memberDto.getPassword())) {
            model.addAttribute("errorMessage", "에러 발생");
            return "edit";
        }
        memberDto.setPassword(memberEditForm.getPassword_new());
        memberDto.setName(memberEditForm.getName());
        memberService.updateMember(memberDto);
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
