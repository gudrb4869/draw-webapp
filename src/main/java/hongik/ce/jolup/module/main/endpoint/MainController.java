package hongik.ce.jolup.module.main.endpoint;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.infra.repository.AccountRepository;
import hongik.ce.jolup.module.account.support.CurrentAccount;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.room.infra.repository.JoinRepository;
import hongik.ce.jolup.module.room.infra.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final AccountRepository accountRepository;
    private final JoinRepository joinRepository;
    private final RoomRepository roomRepository;

    @GetMapping("/")
    public String index(@CurrentAccount Account account, Model model,
                        @PageableDefault(size = 9, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        if (account != null) {
            model.addAttribute(account);
            Page<Join> joins = joinRepository.findWithJoinByAccount(account, pageable);
            model.addAttribute("joins", joins);
            /*log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
            joins.getTotalElements(), joins.getTotalPages(), joins.getSize(),
            joins.getNumber(), joins.getNumberOfElements());*/
            List<Room> rooms = joins.getContent().stream().map(Join::getRoom).collect(Collectors.toList());
            model.addAttribute("rooms", rooms);
            return "home";
        }
        model.addAttribute("roomList", roomRepository.findFirst9ByRevealedOrderByCreatedDateDesc(true));
        return "index";
    }

    @GetMapping("/login")
    public String login(@CurrentAccount Account account) {
        if (account != null) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/search")
    public String search(String category, String keyword, Model model,
                             @PageableDefault(size = 9, sort = "createdDate", direction = Sort.Direction.ASC) Pageable pageable) {
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        if (category.equals("room")) {
            Page<Room> roomPage = roomRepository.findByKeyword(keyword, pageable);
            model.addAttribute("roomPage", roomPage);
        } else if (category.equals("member")) {
            Page<Account> memberPage = accountRepository.findByNameContainingIgnoreCase(keyword, pageable);
            model.addAttribute("memberPage", memberPage);
        }
        return "search";
        /*model.addAttribute("sortProperty", pageable.getSort().toString().contains("publishedDateTime")
                ? "publishedDateTime"
                : "memberCount");*/
    }
}
