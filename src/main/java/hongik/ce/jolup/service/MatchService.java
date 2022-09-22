package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.CompetitionOption;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.repository.CompetitionRepository;
import hongik.ce.jolup.repository.MatchRepository;
import hongik.ce.jolup.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;
    private final MatchRepository matchRepository;

    @Transactional
    public void saveMatches(List<Long> memberIds, Long competitionId, CompetitionOption option) {
        List<Member> members = memberRepository.findAllById(memberIds);
        Competition competition = competitionRepository.findById(competitionId).orElse(null);

        assert competition != null;
        if (competition.getType().equals(CompetitionType.LEAGUE)) {
            Collections.shuffle(members);
            int count = members.size();
            if (count % 2 == 1) {
                for (int i = 0; i < count; i++) {
                    for (int j = 0; j < count/2; j++) {
                        Match match = Match.builder().competition(competition)
                                .home(i % 2 == 0 ? members.get((i + j) % count) : members.get((i + count - j - 2) % count))
                                .away(i % 2 == 0 ? members.get((i + count - j - 2) % count) : members.get((i + j) % count))
                                .matchStatus(MatchStatus.READY)
                                .roundNo(i)
                                .matchNo(j)
//                                .score(Score.builder().homeScore(0).awayScore(0).build())
                                .build();
                        matchRepository.save(match);
                    }
                }
                if (option.equals(CompetitionOption.DOUBLE)) {
                    for (int i = 0; i < count; i++) {
                        for (int j = 0; j < count/2; j++) {
                            Match match = Match.builder().competition(competition)
                                    .home(i % 2 == 0 ? members.get((i + count - j - 2) % count) : members.get((i + j) % count))
                                    .away(i % 2 == 0 ? members.get((i + j) % count) : members.get((i + count - j - 2) % count))
                                    .matchStatus(MatchStatus.READY)
                                    .roundNo(count + i)
                                    .matchNo(j)
                                    .homeScore(0).awayScore(0)
//                                    .score(Score.builder().homeScore(0).awayScore(0).build())
                                    .build();
                            matchRepository.save(match);
                        }
                    }
                }
            }
            else {
                int j;
                Random random = new Random();
                Member fixed = members.remove(random.nextInt(count));
                for (int i = 0; i < count - 1; i++) {
                    for (j = 0; j < count/2 - 1; j++) {
                        Match match = Match.builder().competition(competition)
                                .home(members.get(i % 2 == 0 ? (i + j) % (count - 1) : (i + count - j - 2) % (count - 1)))
                                .away(members.get(i % 2 == 0 ? (i + count - j - 2) % (count - 1) : (i + j) % (count - 1)))
                                .matchStatus(MatchStatus.READY)
                                .roundNo(i)
                                .matchNo(j)
                                .homeScore(0).awayScore(0)
//                                .score(Score.builder().homeScore(0).awayScore(0).build())
                                .build();
                        matchRepository.save(match);
                    }
                    Match match = Match.builder().competition(competition)
                            .home(i % 2 == 0 ? members.get((i + j) % (count - 1)) : fixed)
                            .away(i % 2 == 0 ? fixed : members.get((i + j) % (count - 1)))
                            .matchStatus(MatchStatus.READY)
                            .roundNo(i)
                            .matchNo(j)
                            .homeScore(0).awayScore(0)
//                            .score(Score.builder().homeScore(0).awayScore(0).build())
                            .build();
                    matchRepository.save(match);
                }
                if (option.equals(CompetitionOption.DOUBLE)) {
                    for (int i = 0; i < count - 1; i++) {
                        for (j = 0; j < count/2 - 1; j++) {
                            Match match = Match.builder().competition(competition)
                                    .home(members.get(i % 2 == 0 ? (i + count - j - 2) % (count - 1) : (i + j) % (count - 1)))
                                    .away(members.get(i % 2 == 0 ? (i + j) % (count - 1) : (i + count - j - 2) % (count - 1)))
                                    .matchStatus(MatchStatus.READY)
                                    .roundNo(count - 1 + i)
                                    .matchNo(j)
                                    .homeScore(0).awayScore(0)
//                                    .score(Score.builder().homeScore(0).awayScore(0).build())
                                    .build();
                            matchRepository.save(match);
                        }
                        Match match = Match.builder().competition(competition)
                                .home(i % 2 == 0 ? fixed : members.get((i + j) % (count - 1)))
                                .away(i % 2 == 0 ? members.get((i + j) % (count - 1)) : fixed)
                                .matchStatus(MatchStatus.READY)
                                .roundNo(count - 1 + i)
                                .matchNo(j)
                                .homeScore(0).awayScore(0)
//                                .score(Score.builder().homeScore(0).awayScore(0).build())
                                .build();
                        matchRepository.save(match);
                    }
                }
            }
        } else if (competition.getType().equals(CompetitionType.TOURNAMENT)) {
            Collections.shuffle(members);
            int count = members.size();
            int round_num = (int)Math.ceil(Math.log(count) / Math.log(2)); // 총 라운드
            int match_num = 1;
            int auto_win_num = (int)Math.pow(2, round_num) - count; // 부전승 인원 수

            Set<Integer> set = new HashSet<>(); //
            while (set.size() < auto_win_num) {
                double value = Math.random() * (int)Math.pow(2, round_num - 1);
                set.add((int) value);
            }
            List<Integer> list = new ArrayList<>(set);
            Collections.sort(list);

            for (int i = 0; i < round_num; i++) {
                for (int j = 0; j < match_num; j++) {
                    if (i == round_num - 1 && auto_win_num > 0 && list.contains(j)) {
                        continue;
                    }
                    Match match = Match.builder().competition(competition)
                            .home(i == round_num - 1 ? members.remove(0) :
                                    (i == round_num - 2 && auto_win_num > 0 && list.contains(j * 2) ? members.remove(0) : null))
                            .away(i == round_num - 1 ? members.remove(0) :
                                    (i == round_num - 2 && auto_win_num > 0 && list.contains(j * 2 + 1) ? members.remove(0) : null))
                            .matchStatus(MatchStatus.READY)
                            .roundNo(i)
                            .matchNo(j)
                            .homeScore(0).awayScore(0)
//                            .score(Score.builder().homeScore(0).awayScore(0).build())
                            .build();
                    matchRepository.save(match);
                }
                match_num *= 2;
            }
        }
    }

    @Transactional
    public void update(Long matchId, MatchStatus matchStatus, Integer homeScore, Integer awayScore) {
        Match match = matchRepository.findById(matchId).orElse(null);
        if (match == null) {
            return;
        }
//        Score score = Score.builder().homeScore(homeScore).awayScore(awayScore).build();
//        match.update(score, matchStatus);
        match.updateMatchStatus(matchStatus);
        match.updateScore(homeScore, awayScore);
    }
    
    @Transactional
    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }

    @Transactional
    public void setNull(Long userId) {
        List<Match> homeMatches = matchRepository.findByHomeId(userId);
        for (Match match : homeMatches) {
            match.updateHome(null);
        }
        List<Match> awayMatches = matchRepository.findByAwayId(userId);
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

    public Match findOne(Long matchId) {
        return matchRepository.findById(matchId).orElse(null);
    }

    public Match findOne(Long competitionId, Integer roundNo, Integer matchNo) {
        return matchRepository.findByCompetitionIdAndRoundNoAndMatchNo(competitionId, roundNo, matchNo).orElse(null);
    }
}