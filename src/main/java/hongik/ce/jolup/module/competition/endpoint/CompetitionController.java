package hongik.ce.jolup.module.competition.endpoint;

import hongik.ce.jolup.module.competition.application.CompetitionService;
import hongik.ce.jolup.module.competition.application.ParticipateService;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.competition.endpoint.form.CompetitionForm;
import hongik.ce.jolup.module.competition.endpoint.validator.CompetitionFormValidator;
import hongik.ce.jolup.module.competition.infra.repository.CompetitionRepository;
import hongik.ce.jolup.module.competition.infra.repository.ParticipateRepository;
import hongik.ce.jolup.module.match.application.LeagueService;
import hongik.ce.jolup.module.match.application.TournamentService;
import hongik.ce.jolup.module.match.infra.repository.MatchRepository;
import hongik.ce.jolup.module.member.support.CurrentMember;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
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

    private final CompetitionService competitionService;
    private final RoomService roomService;
    private final ParticipateService participateService;
    private final LeagueService leagueService;
    private final TournamentService tournamentService;
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
//        competitionFormValidator.validateMembers(competitionForm.getMembers(), bindingResult, members);
        if (bindingResult.hasErrors()) {
            model.addAttribute(member);
            model.addAttribute(room);
            model.addAttribute("members", members);
            return "competition/form";
        }
        Competition competition = competitionService.createCompetition(room, competitionForm);

        Set<Long> formMembers = competitionForm.getMembers();
        List<Member> memberList = members.stream().filter(m -> formMembers.contains(m.getId())).collect(Collectors.toList());
        participateService.createRankingTables(memberList, competition);
        switch (competitionForm.getType()) {
            case LEAGUE:
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
                                       @PageableDefault(size = 8, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
                                       Model model) {
        Room room = roomService.getRoom(member, roomId);
        model.addAttribute(member);
        model.addAttribute(room);
        Page<Competition> competitions = competitionRepository.findByRoom(room, pageable);
        model.addAttribute("competitions", competitions);
        return "room/competitions";
    }

    @GetMapping("/{competitionId}")
    public String viewCompetition(@CurrentMember Member member, @PathVariable Long roomId, @PathVariable Long competitionId,
                                  @PageableDefault(size = 9, sort = {"round", "number"}, direction = Sort.Direction.ASC) Pageable pageable, Model model) {

        Room room = roomService.getRoom(roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        model.addAttribute(room);
        model.addAttribute(member);
        model.addAttribute(competition);
        String type = competition.getType().name();
        model.addAttribute("type", type);
        log.info("type = {}", type);
        if (type.equals("LEAGUE")) {
//            LinkedHashMap<Integer, List<Match>> hashMap = new LinkedHashMap<>();
            Page<Match> matches = matchRepository.findMatchWithAllByCompetition(competition, pageable);
            log.info("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
                    matches.getTotalElements(), matches.getTotalPages(), matches.getSize(),
                    matches.getNumber(), matches.getNumberOfElements());
            /*for (Match leagueGame : matches) {
                hashMap.computeIfAbsent(leagueGame.getRound(), k -> new ArrayList<>()).add(leagueGame);
            }*/
//            model.addAttribute("hashMap", hashMap);
            model.addAttribute("matches", matches);
        } else if (type.equals("TOURNAMENT")) {
            LinkedHashMap<Integer, LinkedHashMap<Integer, Match>> matches = new LinkedHashMap<>();
            matchRepository.findMatchWithAllByCompetition(competition)
                    .forEach(match -> matches.computeIfAbsent(match.getRound(), k -> new LinkedHashMap<>()).put(match.getNumber(), match));
            model.addAttribute("matches", matches);
        }
        return "competition/view";
    }

    @GetMapping("/{competitionId}/ranking")
    public String viewCompetitionRanking(@CurrentMember Member member, @PathVariable Long roomId, @PathVariable Long competitionId, Model model) {

        Room room = roomService.getRoom(roomId);
        Competition competition = competitionService.getCompetition(room, competitionId);
        List<Participate> ranking = participateRepository.findLeagueRankingByCompetition(competition);
        model.addAttribute(room);
        model.addAttribute(member);
        model.addAttribute(competition);
        model.addAttribute("ranking", ranking);
        return "competition/ranking";
    }
}
