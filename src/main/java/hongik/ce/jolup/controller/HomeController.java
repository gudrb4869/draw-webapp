package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.service.BelongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BelongService belongService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal Member member,
                        @PageableDefault Pageable pageable, Model model) {
        log.info("home controller");
        if (isAuthenticated()) {
            Page<Belong> belongList = belongService.findByMemberId(member.getId(), pageable);
            model.addAttribute("belongList", belongList);
            log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
            belongList.getTotalElements(), belongList.getTotalPages(), belongList.getSize(),
            belongList.getNumber(), belongList.getNumberOfElements());
            return "rooms/list";
        }
        return "index";
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
