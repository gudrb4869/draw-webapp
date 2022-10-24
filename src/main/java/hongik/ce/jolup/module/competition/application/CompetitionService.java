package hongik.ce.jolup.module.competition.application;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.competition.endpoint.form.CompetitionForm;
import hongik.ce.jolup.module.competition.event.CompetitionCreatedEvent;
import hongik.ce.jolup.module.competition.infra.repository.CompetitionRepository;
import hongik.ce.jolup.module.competition.infra.repository.ParticipateRepository;
import hongik.ce.jolup.module.match.domain.entity.Match;
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
        List<Participate> participates = memberList.stream().map(member -> Participate.from(member, competition)).collect(Collectors.toList());

        Collections.shuffle(memberList);
        Set<Match> matches = new HashSet<>();
        if (competition.isLeague()) {
            Random random = new Random();
            Member fixed = null;
            if (memberList.size() % 2 == 0) {
                fixed = memberList.remove(random.nextInt(memberList.size()));
            }
            int n = memberList.size();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n / 2; j++) {
                    int first = (i + j) % n;
                    int second = (i + n - j - 2) % n;
                    if (i % 2 == 0) {
                        matches.add(Match.from(competition, memberList.get(first), memberList.get(second), i, j));
                        if (competition.getType().equals(CompetitionType.DOUBLE_ROUND_ROBIN)) {
                            matches.add(Match.from(competition, memberList.get(second), memberList.get(first), n + i, j));
                        }
                    } else {
                        matches.add(Match.from(competition, memberList.get(second), memberList.get(first), i, j));
                        if (competition.getType().equals(CompetitionType.DOUBLE_ROUND_ROBIN)) {
                            matches.add(Match.from(competition, memberList.get(first), memberList.get(second), n + i, j));
                        }
                    }
                }
                if (fixed != null) {
                    int j = n / 2;
                    int last = (i + n - 1) % n;
                    if (i % 2 == 0) {
                        matches.add(Match.from(competition, memberList.get(last), fixed, i, j));
                        if (competition.getType().equals(CompetitionType.DOUBLE_ROUND_ROBIN)) {
                            matches.add(Match.from(competition, fixed, memberList.get(last), n + i, j));
                        }
                    } else {
                        matches.add(Match.from(competition, fixed, memberList.get(last), i, j));
                        if (competition.getType().equals(CompetitionType.DOUBLE_ROUND_ROBIN)) {
                            matches.add(Match.from(competition, memberList.get(last), fixed, n + i, j));
                        }
                    }
                }
            }
        }
        else if (competition.isTournament()) {
            int n = memberList.size();
            int round = (int)Math.ceil(Math.log(n) / Math.log(2)); // 총 라운드
            int walkover = (int)Math.pow(2, round) - n; // 부전승 인원 수

            Set<Integer> set = new HashSet<>(); //
            while (set.size() < walkover) {
                double value = Math.random() * (int)Math.pow(2, round - 1);
                set.add((int) value);
            }
            List<Integer> list = new ArrayList<>(set);
            Collections.sort(list);

            int number = 1;
            for (int i = 0; i < round; i++) {
                for (int j = 0; j < number; j++) {
                    if (i == round - 1 && walkover > 0 && list.contains(j)) {
                        continue;
                    }
                    if ((i == round - 1) || (i == round - 2 && walkover > 0 && list.contains(j * 2))) {
                        matches.add(Match.from(competition, memberList.remove(0), memberList.remove(0), i, j));
                    } else {
                        matches.add(Match.from(competition, null, null, i, j));
                    }
                }
                number *= 2;
            }
        }
        participateRepository.saveAll(participates);
        matchRepository.saveAll(matches);
        eventPublisher.publishEvent(new CompetitionCreatedEvent(competition,
                "모임 '" + competition.getRoom().getTitle() + "'에서 새로운 대회 '" + competition.getTitle() + "'가 생성되었습니다.", members));
        return competition;
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

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() > 0 && newTitle.length() <= 50;
    }

    public void updateCompetitionTitle(Competition competition, String newTitle) {
        competition.updateTitle(newTitle);
    }

    public void remove(Competition competition) {
        if (!competition.isRemovable()) {
            throw new IllegalStateException("대회를 삭제할 수 없습니다.");
        }
        competitionRepository.delete(competition);
    }
}
