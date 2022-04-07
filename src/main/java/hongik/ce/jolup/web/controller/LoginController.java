package hongik.ce.jolup.web.controller;

import hongik.ce.jolup.service.AccountService;
import hongik.ce.jolup.web.dto.AccountSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Controller
public class LoginController {
    private final AccountService accountService;

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(AccountSaveRequestDto requestDto) {
        accountService.save(requestDto);
        return "redirect:/";
    }

    /*@GetMapping("/login")
    public String login() {
        return "login";
    }*/

    /*@PostMapping("/login")
    public String login(String userId, String password) {
        return "redirect:/myroom";
    }*/

    @GetMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder
                .getContext().getAuthentication());
        return "redirect:/";
    }
}
