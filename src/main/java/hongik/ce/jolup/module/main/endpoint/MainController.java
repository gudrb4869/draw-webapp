package hongik.ce.jolup.module.main.endpoint;

import hongik.ce.jolup.module.member.support.CurrentMember;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.member.domain.entity.Member;
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

    private final JoinRepository joinRepository;
    private final RoomRepository roomRepository;

    @GetMapping("/")
    public String index(@CurrentMember Member member, Model model,
                        @PageableDefault(size = 8, sort = "createdDate", direction = Sort.Direction.ASC) Pageable pageable) {
        if (member != null) {
            model.addAttribute("member", member);
            Page<Join> joins = joinRepository.findWithJoinByMemberId(member.getId(), pageable);
            model.addAttribute("joins", joins);
            log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
            joins.getTotalElements(), joins.getTotalPages(), joins.getSize(),
            joins.getNumber(), joins.getNumberOfElements());
            List<Room> rooms = joins.getContent().stream().map(Join::getRoom).collect(Collectors.toList());
            model.addAttribute("rooms", rooms);
            return "home";
        }
        return "index";
    }

    @GetMapping("/login")
    public String login(@CurrentMember Member member) {
        if (member != null) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/search/room")
    public String searchRoom(String keyword, Model model,
                              @PageableDefault(size = 8, sort = "createdDate", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Room> roomPage = roomRepository.findByKeyword(keyword, pageable);
        model.addAttribute("roomPage", roomPage);
        model.addAttribute("keyword", keyword);
        /*model.addAttribute("sortProperty", pageable.getSort().toString().contains("publishedDateTime")
                ? "publishedDateTime"
                : "memberCount");*/
        return "search";
    }
}
