package hongik.ce.jolup.module.account.endpoint;

import hongik.ce.jolup.module.account.application.FollowService;
import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.domain.entity.Follow;
import hongik.ce.jolup.module.account.endpoint.form.SignupForm;
import hongik.ce.jolup.module.account.application.AccountService;
import hongik.ce.jolup.module.account.endpoint.validator.SignupFormValidator;
import hongik.ce.jolup.module.account.infra.repository.FollowRepository;
import hongik.ce.jolup.module.account.support.CurrentUser;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignupFormValidator signupFormValidator;
    private final AccountService accountService;
    private final FollowRepository followRepository;
    private final FollowService followService;

    @InitBinder("signupForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupFormValidator);
    }

    @GetMapping("/signup")
    public String signupForm(@CurrentUser Account account, Model model) {
        if (account != null) {
            return "redirect:/";
        }
        model.addAttribute(new SignupForm());
        return "account/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupForm signupForm,
                         BindingResult result, RedirectAttributes attributes) {

        if (result.hasErrors()) {
            return "account/signup";
        }
        accountService.signup(signupForm);
        attributes.addFlashAttribute("message", "회원가입에 성공했습니다.");
        return "redirect:/login";
    }

    @GetMapping("/profile/{id}")
    public String profile(@CurrentUser Account account, @PathVariable Long id, Model model) {
        Account profileAccount = accountService.getAccount(id);
        log.info("profileMember = {}", profileAccount);
        List<Account> followings = followRepository.findByFollowing(profileAccount)
                .stream().map(Follow::getFollower)
                .collect(Collectors.toList());
        List<Account> followers = followRepository.findByFollower(profileAccount)
                .stream().map(Follow::getFollowing)
                .collect(Collectors.toList());
        model.addAttribute("account", account);
        model.addAttribute("profileAccount", profileAccount);
        model.addAttribute("isOwner", profileAccount.equals(account));
        model.addAttribute("isFollowing", followService.isFollow(account, profileAccount));
        model.addAttribute("followings", followings);
        model.addAttribute("followers", followers);
        return "account/profile";
    }

    @PostMapping("/profile/{id}/follow")
//    @ResponseStatus(HttpStatus.OK)
    public String follow(@CurrentUser Account account, @PathVariable Long id) {
        Account byId = accountService.getAccount(id);
        if (byId.equals(account)) {
            throw new IllegalStateException("잘못된 접근입니다.");
        }
        followService.createFollow(account, byId);
        return "redirect:/profile/" + byId.getId();
    }

    @PostMapping("/profile/{id}/unfollow")
//    @ResponseStatus(HttpStatus.OK)
    public String unfollow(@CurrentUser Account account, @PathVariable Long id) {
        Account byId = accountService.getAccount(id);
        if (byId.equals(account)) {
            throw new IllegalStateException("잘못된 접근입니다.");
        }
        followService.deleteFriends(account, byId);
        return "redirect:/profile/" + byId.getId();
    }
}
