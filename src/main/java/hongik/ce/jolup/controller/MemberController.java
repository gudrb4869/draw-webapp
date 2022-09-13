package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.service.MemberService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        model.addAttribute("memberForm", new CreateMemberForm());
        return "members/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("memberForm") @Valid CreateMemberForm form,
                         BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "members/signup";
        }

        try {
            memberService.saveMember(form.getEmail(), form.getPassword(), form.getName());
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "members/signup";
        }
        return "redirect:/login";
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

    @GetMapping("/edit")
    public String updateForm(Model model, @AuthenticationPrincipal Member member) {
        model.addAttribute("memberForm", new UpdateMemberForm(member.getName()));
        return "members/update";
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute("memberForm") @Valid UpdateMemberForm form,
                         BindingResult result, @AuthenticationPrincipal Member member,
                         Model model) {
        if (result.hasErrors()) {
            return "members/update";
        }
        try {
            memberService.updateMember(member.getId(), form.getPassword_current(), form.getPassword_new(), form.getName());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(member.getEmail(), form.getPassword_new()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "members/update";
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

    @Getter @Setter @ToString @Builder
    @NoArgsConstructor @AllArgsConstructor
    private static class CreateMemberForm {
//        @NotBlank(message = "아이디는 필수 입력 값입니다!")
//        @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
        @Pattern(regexp = "^[a-zA-Z]{1}[a-zA-Z0-9_]{3,19}", message = "아이디는 4~20자 영문 대소문자, 숫자를 사용하세요.")
        private String email;

//        @NotBlank(message = "비밀번호는 필수 입력 값입니다!")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대소문자, 숫자, 특수문자를 사용하세요.")
        private String password;

        /*@NotBlank(message = "비밀번호 확인은 필수 입력 값입니다!")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대소문자, 숫자, 특수문자를 사용하세요.")
        private String password_confirm;*/

        @NotBlank(message = "이름은 필수 입력 값입니다!")
        private String name;
    }

    @Getter @Setter @Builder @ToString
    @NoArgsConstructor @AllArgsConstructor
    private static class UpdateMemberForm {
//        @NotBlank(message = "비밀번호는 필수 입력 값입니다!")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String password_current;

//        @NotBlank(message = "비밀번호는 필수 입력 값입니다!")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String password_new;

        @NotBlank(message = "이름은 필수 입력 값입니다!")
        private String name;

        public UpdateMemberForm(String name) {
            this.name = name;
        }
    }
}
