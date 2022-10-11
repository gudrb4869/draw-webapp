package hongik.ce.jolup.module.competition.application;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.CompetitionOption;
import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.competition.endpoint.form.CompetitionForm;
import hongik.ce.jolup.module.competition.event.CompetitionCreatedEvent;
import hongik.ce.jolup.module.competition.infra.repository.CompetitionRepository;
import hongik.ce.jolup.module.competition.infra.repository.ParticipateRepository;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.match.domain.entity.Status;
import hongik.ce.jolup.module.match.infra.repository.MatchRepository;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompetitionService {

    private final ParticipateRepository participateRepository;
    private final MatchRepository matchRepository;
    private final CompetitionRepository competitionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Competition createCompetition(List<Member> members, Room room, CompetitionForm competitionForm) {
        Competition competition = competitionRepository.save(Competition.from(competitionForm, room));

        List<Member> memberList = members.stream().filter(m -> competitionForm.getMembers().contains(m.getId())).collect(Collectors.toList());
        List<Participate> participates = memberList.stream().map(m -> Participate.builder().member(m).competition(competition)
                .win(0).draw(0).lose(0).goalFor(0).goalAgainst(0).build()).collect(Collectors.toList());

        Collections.shuffle(memberList);
        Set<Match> matches = new HashSet<>();
        int count = memberList.size();
        if (competition.getType().equals(CompetitionType.LEAGUE)) {
            if (count % 2 == 1) {
                for (int i = 0; i < count; i++) {
                    for (int j = 0; j < count/2; j++) {
                        Match match = Match.builder().competition(competition)
                                .home(i % 2 == 0 ? memberList.get((i + j) % count) : memberList.get((i + count - j - 2) % count))
                                .away(i % 2 == 0 ? memberList.get((i + count - j - 2) % count) : memberList.get((i + j) % count))
                                .status(Status.BEFORE)
                                .round(i)
                                .number(j)
                                .homeScore(0).awayScore(0)
                                .build();
                        matches.add(match);
                    }
                }
                if (competitionForm.getOption().equals(CompetitionOption.DOUBLE)) {
                    for (int i = 0; i < count; i++) {
                        for (int j = 0; j < count/2; j++) {
                            Match match = Match.builder().competition(competition)
                                    .home(i % 2 == 0 ? memberList.get((i + count - j - 2) % count) : memberList.get((i + j) % count))
                                    .away(i % 2 == 0 ? memberList.get((i + j) % count) : memberList.get((i + count - j - 2) % count))
                                    .status(Status.BEFORE)
                                    .round(count + i)
                                    .number(j)
                                    .homeScore(0).awayScore(0)
                                    .build();
                            matches.add(match);
                        }
                    }
                }
            }
            else {
                int j;
                Random random = new Random();
                Member fixed = memberList.remove(random.nextInt(count));
                for (int i = 0; i < count - 1; i++) {
                    for (j = 0; j < count/2 - 1; j++) {
                        Match match = Match.builder().competition(competition)
                                .home(memberList.get(i % 2 == 0 ? (i + j) % (count - 1) : (i + count - j - 2) % (count - 1)))
                                .away(memberList.get(i % 2 == 0 ? (i + count - j - 2) % (count - 1) : (i + j) % (count - 1)))
                                .status(Status.BEFORE)
                                .round(i)
                                .number(j)
                                .homeScore(0).awayScore(0)
                                .build();
                        matches.add(match);
                    }
                    Match match = Match.builder().competition(competition)
                            .home(i % 2 == 0 ? memberList.get((i + j) % (count - 1)) : fixed)
                            .away(i % 2 == 0 ? fixed : memberList.get((i + j) % (count - 1)))
                            .status(Status.BEFORE)
                            .round(i)
                            .number(j)
                            .homeScore(0).awayScore(0)
                            .build();
                    matches.add(match);
                }
                if (competitionForm.getOption().equals(CompetitionOption.DOUBLE)) {
                    for (int i = 0; i < count - 1; i++) {
                        for (j = 0; j < count/2 - 1; j++) {
                            Match match = Match.builder().competition(competition)
                                    .home(memberList.get(i % 2 == 0 ? (i + count - j - 2) % (count - 1) : (i + j) % (count - 1)))
                                    .away(memberList.get(i % 2 == 0 ? (i + j) % (count - 1) : (i + count - j - 2) % (count - 1)))
                                    .status(Status.BEFORE)
                                    .round(count - 1 + i)
                                    .number(j)
                                    .homeScore(0).awayScore(0)
                                    .build();
                            matches.add(match);
                        }
                        Match match = Match.builder().competition(competition)
                                .home(i % 2 == 0 ? fixed : memberList.get((i + j) % (count - 1)))
                                .away(i % 2 == 0 ? memberList.get((i + j) % (count - 1)) : fixed)
                                .status(Status.BEFORE)
                                .round(count - 1 + i)
                                .number(j)
                                .homeScore(0).awayScore(0)
                                .build();
                        matches.add(match);
                    }
                }
            }
        }
        else if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
            int round = (int)Math.ceil(Math.log(count) / Math.log(2)); // 총 라운드
            int number = 1;
            int auto_win_num = (int)Math.pow(2, round) - count; // 부전승 인원 수

            Set<Integer> set = new HashSet<>(); //
            while (set.size() < auto_win_num) {
                double value = Math.random() * (int)Math.pow(2, round - 1);
                set.add((int) value);
            }
            List<Integer> list = new ArrayList<>(set);
            Collections.sort(list);

            for (int i = 0; i < round; i++) {
                for (int j = 0; j < number; j++) {
                    if (i == round - 1 && auto_win_num > 0 && list.contains(j)) {
                        continue;
                    }
                    Match match = Match.builder().competition(competition)
                            .home(i == round - 1 ? memberList.remove(0) :
                                    (i == round - 2 && auto_win_num > 0 && list.contains(j * 2) ? memberList.remove(0) : null))
                            .away(i == round - 1 ? memberList.remove(0) :
                                    (i == round - 2 && auto_win_num > 0 && list.contains(j * 2 + 1) ? memberList.remove(0) : null))
                            .status(Status.BEFORE)
                            .round(i)
                            .number(j)
                            .homeScore(0).awayScore(0)
                            .build();
                    matches.add(match);
                }
                number *= 2;
            }
        }
        participateRepository.saveAll(participates);
        matchRepository.saveAll(matches);
//        eventPublisher.publishEvent(new RoomUpdatedEvent(competition.getRoom(), "'" + competition.getTitle() + "' 대회가 생성되었습니다."));
        return competition;
    }

    public void SendAlarm(Competition competition, Set<Member> members) {
        eventPublisher.publishEvent(new CompetitionCreatedEvent(competition, "새로운 대회가 생성되었습니다.", members));
    }

    public Competition getCompetition(Room room, Long competitionId) {
        Competition competition = competitionRepository.findCompetitionWithRoomAndParticipatesById(competitionId).orElse(null);
        checkExistsCompetition(room, competition);
        return competition;
    }

    private void checkExistsCompetition(Room room, Competition competition) {
        if (competition == null || !competition.getRoom().equals(room)) {
            throw new IllegalArgumentException("존재하지 않는 대회입니다.");
        }
    }
}
