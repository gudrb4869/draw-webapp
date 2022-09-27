package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.service.JoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JoinService joinService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal Member member, Model model, String keyword,
                        @PageableDefault(size = 9, sort = "createdDate", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("home controller");
        if (member != null) {
            model.addAttribute("member", member);
            Page<Join> joins = joinService.findByMemberId(member.getId(), pageable);
            model.addAttribute("joins", joins);
            log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
            joins.getTotalElements(), joins.getTotalPages(), joins.getSize(),
            joins.getNumber(), joins.getNumberOfElements());
            return "home";
        }
        return "index";
    }
}
