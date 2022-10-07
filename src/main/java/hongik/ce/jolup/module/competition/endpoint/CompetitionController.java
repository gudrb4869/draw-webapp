package hongik.ce.jolup.module.competition.endpoint;

import hongik.ce.jolup.module.competition.application.CompetitionService;
import hongik.ce.jolup.module.competition.application.LeagueTableService;
import hongik.ce.jolup.module.competition.endpoint.form.CompetitionForm;
import hongik.ce.jolup.module.competition.endpoint.validator.CompetitionFormValidator;
import hongik.ce.jolup.module.competition.infra.repository.CompetitionRepository;
import hongik.ce.jolup.module.match.application.LeagueService;
import hongik.ce.jolup.module.match.application.TournamentService;
import hongik.ce.jolup.module.member.support.CurrentMember;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Grade;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.LeagueTable;
import hongik.ce.jolup.module.member.application.MemberService;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.room.application.JoinService;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("rooms/{roomId}/competitions")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final RoomService roomService;
    private final JoinService joinService;
    private final LeagueTableService leagueTableService;
    private final LeagueService leagueService;
    private final TournamentService tournamentService;
    private final CompetitionRepository competitionRepository;
    private final CompetitionFormValidator competitionFormValidator;

    @InitBinder("competitionForm")
    public void competitionFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(competitionFormValidator);
    }

    @GetMapping("/create")
    public String createForm(@CurrentMember Member member, @PathVariable Long roomId, Model model) {
        Room room = roomService.getRoomToUpdate(member, roomId);
        model.addAttribute(member);
        model.addAttribute(room);
        List<Member> members = room.getJoins().stream().map(Join::getMember)
                .collect(Collectors.toList());
        model.addAttribute("members", members);
        model.addAttribute(new CompetitionForm());
        return "competition/form";
    }

    @PostMapping("/create")
    public String create(@CurrentMember Member member, @PathVariable Long roomId, Model model,
                         @Valid CompetitionForm competitionForm, BindingResult bindingResult, RedirectAttributes attributes) {

        Room room = roomService.getRoomToUpdate(member, roomId);
        List<Member> members = room.getJoins().stream().map(Join::getMember)
                .collect(Collectors.toList());
        if (bindingResult.hasErrors()) {
            model.addAttribute(member);
            model.addAttribute(room);
            model.addAttribute("members", members);
            return "competition/form";
        }
        Competition competition = competitionService.createCompetition(room, competitionForm);

        Set<Long> formMembers = competitionForm.getMembers();
        List<Member> memberList = members.stream().filter(m -> formMembers.contains(m.getId())).collect(Collectors.toList());
        switch (competitionForm.getType()) {
            case LEAGUE:
                leagueTableService.createLeagueTable(memberList, competition);
                leagueService.createMatches(memberList, competition);
                break;
            case TOURNAMENT:
                tournamentService.createMatches(memberList, competition);
                break;
        }
        attributes.addFlashAttribute("message", "대회를 만들었습니다.");
        return "redirect:/rooms/" + room.getId() + "/competitions";
    }

    @GetMapping
    public String viewRoomCompetitions(@CurrentMember Member member, @PathVariable Long roomId,
                                       @PageableDefault(size = 8, sort = {"createdDate"}, direction = Sort.Direction.DESC) Pageable pageable,
                                       Model model) {
        Room room = roomService.getRoom(member, roomId);
        model.addAttribute(member);
        model.addAttribute(room);
        Page<Competition> competitions = competitionRepository.findByRoom(room, pageable);
        model.addAttribute("competitions", competitions);
        return "room/competitions";
    }

    @GetMapping("/{competitionId}")
    public String viewCompetition(@PathVariable Long roomId, @PathVariable Long competitionId,
                                  @CurrentMember Member member, @PageableDefault Pageable pageable, Model model) {

        Room room = roomService.getRoom(member, roomId);
        Competition competition = competitionService.getCompetition(member, room, competitionId);

        model.addAttribute(member);
        model.addAttribute(competition);
        model.addAttribute("type", competition.getType().name());
        if (competition.getType().equals(CompetitionType.LEAGUE)) {
            List<LeagueTable> leagueTables = leagueTableService.findByCompetitionSort(competitionId);
            model.addAttribute("leagueTables", leagueTables);
            LinkedHashMap<Integer, List<Match>> hashMap = new LinkedHashMap<>();
            Page<Match> matches = leagueService.findByCompetitionId(competitionId, leagueTables.size(), pageable);
            log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
                    matches.getTotalElements(), matches.getTotalPages(), matches.getSize(),
                    matches.getNumber(), matches.getNumberOfElements());
            for (Match leagueGame : matches) {
                hashMap.computeIfAbsent(leagueGame.getRound(), k -> new ArrayList<>()).add(leagueGame);
            }
            model.addAttribute("hashMap", hashMap);
            model.addAttribute("matches", matches);
            return "competition/league";
        } else if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
            LinkedHashMap<Integer, LinkedHashMap<Integer, Match>> hashMap = new LinkedHashMap<>();
            List<Match> matches = tournamentService.findByCompetition(competitionId);
            for (Match match : matches) {
                hashMap.computeIfAbsent(match.getRound(), k -> new LinkedHashMap<>()).put(match.getNumber(), match);
            }
            model.addAttribute("hashMap", hashMap);
            return "competition/tournament";
        }
        return "competition/view";
    }

    @DeleteMapping("/{competitionId}")
    public String delete(@CurrentMember Member member, @PathVariable Long roomId, @PathVariable Long competitionId, RedirectAttributes attributes) {

        log.info("DELETE : competitionId = {}", competitionId);

        Competition competition = competitionService.findOne(competitionId, roomId);
        if (competition == null) {
            return "error";
        }

        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        competitionService.deleteCompetition(competitionId);
        attributes.addFlashAttribute("message", "대회를 삭제했습니다.");
        return "redirect:/rooms/{roomId}";
    }

    @GetMapping("/{competitionId}/update")
    public String updateForm(@CurrentMember Member member, @PathVariable Long roomId, @PathVariable Long competitionId, Model model) {
        Competition competition = competitionService.findOne(competitionId, roomId);
        log.info("GET : editCompetition, competition = {}", competition);
        if (competition == null) {
            return "error";
        }

        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }
        model.addAttribute("form", new UpdateCompetitionForm(competition.getId(), competition.getTitle()));
        return "competition/updateCompetitionForm";
    }

    @PostMapping("/{competitionId}/update")
    public String update(@CurrentMember Member member, @PathVariable Long roomId, @PathVariable Long competitionId,
                         @Valid UpdateCompetitionForm competitionForm, BindingResult result, RedirectAttributes attributes) {
        log.info("POST : UpdateCompetitionForm = {}", competitionForm);

        if (result.hasErrors()) {
            return "competitions/update";
        }

        if (competitionService.findOne(competitionId, roomId) == null) {
            return "error";
        }

        Join myJoin = joinService.findOne(member.getId(), roomId);
        if (myJoin == null || !myJoin.getGrade().equals(Grade.ADMIN)) {
            return "error";
        }

        competitionService.updateCompetition(competitionForm.getId(), competitionForm.getName());
        attributes.addFlashAttribute("message", "대회 정보를 수정했습니다.");
        return "redirect:/rooms/{roomId}/competitions/{competitionId}";
    }

    @Getter @Setter @AllArgsConstructor
    @NoArgsConstructor @ToString
    private static class UpdateCompetitionForm {
        @NotNull(message = "대회고유번호는 필수 입력 값입니다.")
        private Long id;

        @NotBlank(message = "대회명은 필수 입력 값입니다.")
        private String name;
    }
}
