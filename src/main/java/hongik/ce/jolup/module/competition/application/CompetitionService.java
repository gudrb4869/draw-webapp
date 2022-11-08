package hongik.ce.jolup.module.competition.application;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.competition.endpoint.form.CompetitionForm;
import hongik.ce.jolup.module.competition.event.CompetitionCreatedEvent;
import hongik.ce.jolup.module.competition.infra.repository.CompetitionRepository;
import hongik.ce.jolup.module.competition.infra.repository.ParticipateRepository;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.match.infra.repository.MatchRepository;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompetitionService {

    private final ParticipateRepository participateRepository;
    private final MatchRepository matchRepository;
    private final CompetitionRepository competitionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Competition createCompetition(List<Account> accounts, Room room, CompetitionForm competitionForm) {
        Competition competition = competitionRepository.save(Competition.from(competitionForm, room));

        List<Account> accountList = accounts.stream().filter(m -> competitionForm.getMembers().contains(m.getId())).collect(Collectors.toList());
        List<Participate> participates = accountList.stream().map(member -> Participate.from(member, competition)).collect(Collectors.toList());

        Collections.shuffle(accountList);
        Set<Match> matches = new HashSet<>();
        if (competition.isLeague()) {
            Random random = new Random();
            Account fixed = null;
            if (accountList.size() % 2 == 0) {
                fixed = accountList.remove(random.nextInt(accountList.size()));
            }
            /*
            참가자 수 : n
            참가자 수가 짝수 일 경우 -> 전체 라운드 수 : n - 1, 라운드당 게임 수 : n / 2
            참가자 수가 홀수 일 경우 -> 전체 라운드 수 : n, 라운드당 게임 수 (n - 1) / 2
             */
            int n = accountList.size();
            for (int i = 0; i < n; i++) { // n은 홀수 전체 라운드 수 : n
                for (int j = 0; j < n / 2; j++) { // n은 홀수, 라운드당 게임 수 : floor((n - 1) / 2) -> n / 2
                    int first = (i + j) % n;
                    int second = (i + n - j - 2) % n;
                    matches.add(Match.from(competition, accountList.get(first), accountList.get(second), i, j));
                    if (competition.getType().equals(CompetitionType.DOUBLE_ROUND_ROBIN)) {
                        matches.add(Match.from(competition, accountList.get(second), accountList.get(first), n + i, j));
                    }
                }
                if (fixed != null) {
                    int j = n / 2;
                    int last = (i + n - 1) % n;
                    if (i % 2 == 0) {
                        matches.add(Match.from(competition, accountList.get(last), fixed, i, j));
                        if (competition.getType().equals(CompetitionType.DOUBLE_ROUND_ROBIN)) {
                            matches.add(Match.from(competition, fixed, accountList.get(last), n + i, j));
                        }
                    } else {
                        matches.add(Match.from(competition, fixed, accountList.get(last), i, j));
                        if (competition.getType().equals(CompetitionType.DOUBLE_ROUND_ROBIN)) {
                            matches.add(Match.from(competition, accountList.get(last), fixed, n + i, j));
                        }
                    }
                }
            }
        }
        else if (competition.isTournament()) {
            int n = accountList.size();
            int round = (int)Math.ceil(Math.log(n) / Math.log(2)); // 총 라운드
            int walkover = (int)Math.pow(2, round) - n; // 부전승 인원 수

            List<Account> seed1 = new ArrayList<>();
            for (int i = 0; i < (int)Math.pow(2, round - 1); i++) {
                seed1.add(accountList.remove(0));
            }
            List<Account> seed2 = new ArrayList<>();
            for (int i = 0; i < n - (int)Math.pow(2, round - 1); i++) {
                seed2.add(accountList.remove(0));
            }
            for (int i = 0; i < walkover; i++) {
                seed2.add(null);
            }
            Collections.shuffle(seed1);
            Collections.shuffle(seed2);

            int number = 1;
            for (int i = 0; i < round; i++) {
                for (int j = 0; j < number; j++) {
                    if (i == round - 1) {
                        Match match = Match.from(competition, seed1.get(j), seed2.get(j), i, j);
                        matches.add(match);
                        if (seed2.get(j) == null) {
                            match.close();
                            Integer nextMatchRound = i - 1;
                            Integer nextMatchNumber = j / 2;
                            Match nextMatch = matches.stream().filter(m -> nextMatchRound.equals(m.getRound()) && nextMatchNumber.equals(m.getNumber()))
                                    .findAny().orElseThrow(() -> new IllegalStateException("다음 라운드 경기를 찾을 수 없습니다."));
                            if (j % 2 == 0) {
                                nextMatch.updateHome(seed1.get(j));
                            } else {
                                nextMatch.updateAway(seed1.get(j));
                            }
                        }
                    }
                    else {
                        matches.add(Match.from(competition, null, null, i, j));
                    }
                }
                number *= 2;
            }
        }
        participateRepository.saveAll(participates);
        matchRepository.saveAll(matches);
        eventPublisher.publishEvent(new CompetitionCreatedEvent(competition,
                "방 '" + competition.getRoom().getTitle() + "'에서 새로운 대회 '" + competition.getTitle() + "'가 생성되었습니다.", accounts));
        return competition;
    }

    public Competition getCompetition(Room room, Long competitionId) {
        return getCompetition(room, competitionRepository.findCompetitionWithRoomAndParticipatesById(competitionId));
    }

    public Competition getCompetitionToUpdateMatch(Room room, Long competitionId) {
        return getCompetition(room, competitionRepository.findCompetitionWithRoomAndMatchesById(competitionId));
    }

    private Competition getCompetition(Room room, Competition competition) {
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

    @Transactional
    public void updateCompetitionTitle(Competition competition, String newTitle) {
        competition.updateTitle(newTitle);
    }

    @Transactional
    public void remove(Competition competition) {
        if (!competition.isRemovable()) {
            throw new IllegalStateException("대회를 삭제할 수 없습니다.");
        }
        competitionRepository.delete(competition);
    }
}
