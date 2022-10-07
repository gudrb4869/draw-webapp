package hongik.ce.jolup.module.match.application;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.match.domain.entity.Status;
import hongik.ce.jolup.module.match.event.MatchUpdatedEvent;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.competition.infra.repository.CompetitionRepository;
import hongik.ce.jolup.module.match.infra.repository.MatchRepository;
import hongik.ce.jolup.module.member.infra.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TournamentService {

    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;
    private final MatchRepository matchRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void createMatches(List<Member> memberList, Competition competition) {
        Collections.shuffle(memberList);
        int count = memberList.size();
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

        Set<Match> matches = new HashSet<>();
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
        matchRepository.saveAll(matches);
    }
    public void save(List<Long> memberIds, Long competitionId) {
        List<Member> members = memberRepository.findAllById(memberIds);
        Competition competition = competitionRepository.findById(competitionId).orElse(null);

        assert competition != null;
        Collections.shuffle(members);
        int count = members.size();
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
                        .home(i == round - 1 ? members.remove(0) :
                                (i == round - 2 && auto_win_num > 0 && list.contains(j * 2) ? members.remove(0) : null))
                        .away(i == round - 1 ? members.remove(0) :
                                (i == round - 2 && auto_win_num > 0 && list.contains(j * 2 + 1) ? members.remove(0) : null))
                        .status(Status.BEFORE)
                        .round(i)
                        .number(j)
                        .homeScore(0).awayScore(0)
                        .build();
                matchRepository.save(match);
            }
            number *= 2;
        }
    }

    public void update(Long id, Long competitionId, Status status, Integer homeScore, Integer awayScore) {
        Match match = matchRepository.findById(id).orElse(null);
        if (match == null) {
            return;
        }

        if (match.getStatus().equals(Status.AFTER)) {
            if (match.getHomeScore() > match.getAwayScore() && homeScore <= awayScore) {
                resetMatches(competitionId, match);
            }
            else if (match.getHomeScore() < match.getAwayScore() && homeScore >= awayScore) {
                resetMatches(competitionId, match);
            }
        }
        if (status.equals(Status.AFTER)) {
            if (homeScore > awayScore) {
                setNextRoundMatch(competitionId, match, match.getHome());
            } else if (homeScore < awayScore) {
                setNextRoundMatch(competitionId, match, match.getAway());
            }
        }

        match.updateStatus(status);
        match.updateScore(homeScore, awayScore);
    }

    public void sendAlarm(Match match, Long roomId, Set<Member> members) {
        eventPublisher.publishEvent(new MatchUpdatedEvent(match, roomId, "경기 결과가 수정되었습니다.", members));
    }

    public void delete(Long id) {
        matchRepository.deleteById(id);
    }

    public void setNull(Long memberId) {
        List<Match> homeMatches = matchRepository.findByHomeId(memberId);
        for (Match match : homeMatches) {
            match.updateHome(null);
        }
        List<Match> awayMatches = matchRepository.findByAwayId(memberId);
        for (Match match : awayMatches) {
            match.updateAway(null);
        }
    }

    public Page<Match> findByCompetition(Long competitionId, int count, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        return matchRepository.findByCompetitionId(competitionId, PageRequest.of(page, count / 2));
    }

    public List<Match> findByCompetition(Long competitionId) {
        return matchRepository.findByCompetitionId(competitionId);
    }

    public Match findByIdAndCompetitionId(Long id, Long competitionId) {
        return matchRepository.findByIdAndCompetitionId(id, competitionId).orElse(null);
    }

    public Match findOne(Long id) {
        return matchRepository.findById(id).orElse(null);
    }

    public Match findOne(Long competitionId, Integer round, Integer number) {
        return matchRepository.findByCompetitionIdAndRoundAndNumber(competitionId, round, number).orElse(null);
    }

    private void setNextRoundMatch(Long competitionId, Match match, Member member) {
        Match nextMatch = matchRepository
                .findByCompetitionIdAndRoundAndNumber(competitionId, match.getRound() - 1, match.getNumber() / 2)
                .orElse(null);
        if (nextMatch != null) {
            if (match.getNumber() % 2 == 0) {
                nextMatch.updateHome(member);
            } else {
                nextMatch.updateAway(member);
            }
        }
    }

    private void resetMatches(Long competitionId, Match match) {
        Match cur = match;
        while (true) {
            Match next = matchRepository
                    .findByCompetitionIdAndRoundAndNumber(competitionId, cur.getRound() - 1, cur.getNumber() / 2)
                    .orElse(null);
            if (next == null) {
                break;
            }
            if (cur.getNumber() % 2 == 0) {
                next.updateHome(null);
            } else {
                next.updateAway(null);
            }
            next.updateStatus(Status.BEFORE);
            next.updateScore(0, 0);
            cur = next;
        }
    }
}