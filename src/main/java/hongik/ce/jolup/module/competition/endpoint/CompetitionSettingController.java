package hongik.ce.jolup.module.competition.endpoint;

import hongik.ce.jolup.module.competition.application.CompetitionService;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.support.CurrentMember;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms/{roomId}/competitions/{competitionId}/settings")
public class CompetitionSettingController {

    private final RoomService roomService;
    private final CompetitionService competitionService;

    @GetMapping
    public String viewCompetitionSetting(@CurrentMember Member member, @PathVariable Long roomId, @PathVariable Long competitionId, Model model) {
        Room room = roomService.getRoomToUpdate(member, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        model.addAttribute(member);
        model.addAttribute(room);
        model.addAttribute(competition);
        return "competition/settings/competition";
    }

    @PostMapping("/competition/title")
    public String updateCompetitionTitle(@CurrentMember Member member, @PathVariable Long roomId, @PathVariable Long competitionId, String newTitle,
                                  Model model, RedirectAttributes attributes) {
        Room room = roomService.getRoomToUpdate(member, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        if (!competitionService.isValidTitle(newTitle)) {
            model.addAttribute(member);
            model.addAttribute(room);
            model.addAttribute(competition);
            model.addAttribute("competitionTitleError", "대회 이름을 다시 입력하세요.");
            return "competition/settings/competition";
        }
        competitionService.updateCompetitionTitle(competition, newTitle);
        attributes.addFlashAttribute("message", "대회명을 수정하였습니다.");
        return "redirect:/rooms/" + room.getId() + "/competitions/" + competition.getId() + "/settings";
    }

    @DeleteMapping("/competition/remove")
    public String removeCompetition(@CurrentMember Member member, @PathVariable Long roomId, @PathVariable Long competitionId) {
        Room room = roomService.getRoomToUpdate(member, roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        competitionService.remove(competition);
        return "redirect:/rooms/" + room.getId() + "/competitions";
    }
}
