package hongik.ce.jolup.web.controller;

import hongik.ce.jolup.service.AccountService;
import hongik.ce.jolup.web.dto.AccountSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
