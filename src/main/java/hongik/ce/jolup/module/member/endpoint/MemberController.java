package hongik.ce.jolup.module.member.endpoint;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.endpoint.form.SignupForm;
import hongik.ce.jolup.module.member.application.MemberService;
import hongik.ce.jolup.module.member.endpoint.validator.SignupFormValidator;
import hongik.ce.jolup.module.member.infra.repository.MemberRepository;
import hongik.ce.jolup.module.member.support.CurrentMember;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final SignupFormValidator signupFormValidator;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @InitBinder("signupForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupFormValidator);
    }

    @GetMapping("/signup")
    public String signupForm(@CurrentMember Member member, Model model) {
        if (member != null) {
            return "redirect:/";
        }
        model.addAttribute(new SignupForm());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupForm signupForm,
                         BindingResult result, RedirectAttributes attributes) {

        if (result.hasErrors()) {
            return "member/signup";
        }
        memberService.signup(signupForm);
        attributes.addFlashAttribute("message", "회원가입에 성공했습니다.");
        return "redirect:/";
    }

    @GetMapping("/profile/{name}")
    public String profile(@PathVariable String name, @CurrentMember Member member, Model model) {
        Member byName = memberService.findByName(name);
        model.addAttribute("member", byName);
        model.addAttribute("isOwner", byName.equals(member));
        return "member/profile";
    }
}
