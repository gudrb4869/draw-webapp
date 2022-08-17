package hongik.ce.jolup.api;

import hongik.ce.jolup.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/emailCheck")
    @ResponseBody
    public boolean emailCheck(@RequestParam("email") String email) {
        boolean result = false;
        if (memberService.findByEmail(email) != null) {
            result = true;
        }
        return result;
    }

    @PostMapping("/nameCheck")
    @ResponseBody
    public boolean nameCheck(@RequestParam("name") String name) {
        boolean result = false;
        if (memberService.findByName(name) != null) {
            result = true;
        }
        return result;
    }
}
