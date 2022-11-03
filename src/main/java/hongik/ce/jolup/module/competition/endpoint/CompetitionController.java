package hongik.ce.jolup.module.competition.endpoint;

import hongik.ce.jolup.module.competition.application.CompetitionService;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.competition.endpoint.form.CompetitionForm;
import hongik.ce.jolup.module.competition.endpoint.validator.CompetitionFormValidator;
import hongik.ce.jolup.module.competition.infra.repository.CompetitionRepository;
import hongik.ce.jolup.module.competition.infra.repository.ParticipateRepository;
import hongik.ce.jolup.module.match.infra.repository.MatchRepository;
import hongik.ce.jolup.module.member.support.CurrentMember;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("rooms/{roomId}/competitions")
public class CompetitionController {

    private final RoomService roomService;
    private final CompetitionService competitionService;
    private final CompetitionRepository competitionRepository;
    private final MatchRepository matchRepository;
    private final ParticipateRepository participateRepository;
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
        Competition competition = competitionService.createCompetition(members, room, competitionForm);

        attributes.addFlashAttribute("message", "대회를 만들었습니다.");
        return "redirect:/rooms/" + room.getId() + "/competitions/" + competition.getId();
    }

    @GetMapping
    public String viewRoomCompetitions(@CurrentMember Member member, @PathVariable Long roomId,
                                       @PageableDefault(size = 8, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
                                       Model model) {
        Room room = roomService.getRoom(member, roomId);
        model.addAttribute(member);
        model.addAttribute(room);
        Page<Competition> competitions = competitionRepository.findCompetitionsByRoom(room, pageable);
        model.addAttribute("competitions", competitions);
        return "room/competitions";
    }

    @GetMapping("/{competitionId}")
    public String viewCompetition(@CurrentMember Member member, @PathVariable Long roomId, @PathVariable Long competitionId,
                                  @PageableDefault(size = 10, sort = "round", direction = Sort.Direction.ASC) Pageable pageable, Model model) {

        Room room = roomService.getRoom(roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        model.addAttribute(room);
        model.addAttribute(member);
        model.addAttribute(competition);
        LinkedHashMap<Integer, List<Match>> matches = new LinkedHashMap<>();
        if (competition.isLeague()) {
            Page<Match> matchPage = matchRepository.findMatchWithAllByCompetition(competition, pageable);
            matchPage.forEach(match -> matches.computeIfAbsent(match.getRound(), k -> new ArrayList<>()).add(match));
            model.addAttribute("matchPage", matchPage);
        } else if (competition.isTournament()) {
            matchRepository.findMatchWithAllByCompetition(competition)
                    .forEach(match -> matches.computeIfAbsent(match.getRound(), k -> new ArrayList<>()).add(match));
        }
        for (Integer key : matches.keySet()) {
            matches.get(key).sort(Comparator.comparing(Match::getNumber));
        }
        model.addAttribute("matches", matches);
        model.addAttribute("first_round", Collections.min(matches.keySet()));
        model.addAttribute("last_round", Collections.max(matches.keySet()));
        return "competition/view";
    }

    @GetMapping("/{competitionId}/ranking")
    public String viewCompetitionRanking(@CurrentMember Member member, @PathVariable Long roomId, @PathVariable Long competitionId, Model model) {

        Room room = roomService.getRoom(roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        if (competition.isTournament()) {
            throw new IllegalArgumentException("토너먼트 모드에서는 순위를 볼 수 없습니다.");
        }
        List<Participate> ranking = participateRepository.findLeagueRankingByCompetition(competition);
        model.addAttribute(room);
        model.addAttribute(member);
        model.addAttribute(competition);
        model.addAttribute("ranking", ranking);
        return "competition/ranking";
    }
}
