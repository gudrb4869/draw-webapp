package hongik.ce.jolup.module.member.endpoint;

import hongik.ce.jolup.module.member.application.FriendService;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.endpoint.form.SignupForm;
import hongik.ce.jolup.module.member.application.MemberService;
import hongik.ce.jolup.module.member.endpoint.validator.SignupFormValidator;
import hongik.ce.jolup.module.member.support.CurrentMember;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final SignupFormValidator signupFormValidator;
    private final MemberService memberService;
    private final FriendService friendService;

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

    @GetMapping("/profile/{id}")
    public String profile(@CurrentMember Member member, @PathVariable Long id, Model model) {
        Member byId = memberService.getMember(id);
        model.addAttribute("member", byId);
        model.addAttribute("isOwner", byId.equals(member));
        model.addAttribute("isFollowing", friendService.isFollow(member, byId));
        return "member/profile";
    }

    @GetMapping("/profile/{id}/follow")
    public String follow(@CurrentMember Member member, @PathVariable Long id) {
        Member byId = memberService.getMember(id);
        if (byId.equals(member)) {
            throw new IllegalStateException("잘못된 접근입니다.");
        }
        friendService.createFriends(member, byId);
        return "redirect:/profile/" + byId.getId();
    }

    @GetMapping("/profile/{id}/unfollow")
    public String unfollow(@CurrentMember Member member, @PathVariable Long id) {
        Member byId = memberService.getMember(id);
        if (byId.equals(member)) {
            throw new IllegalStateException("잘못된 접근입니다.");
        }
        friendService.deleteFriends(member, byId);
        return "redirect:/profile/" + byId.getId();
    }
}
