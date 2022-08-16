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
    public int emailCheck(@RequestParam("email") String email) {
        int result = 0;
        if (memberService.findByEmail(email) != null) {
            result = 1;
        }
        return result;
    }

    @PostMapping("/nameCheck")
    @ResponseBody
    public int nameCheck(@RequestParam("name") String name) {
        int result = 0;
        if (memberService.findByName(name) != null) {
            result = 1;
        }
        return result;
    }
}
