package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.score.Score;
import hongik.ce.jolup.dto.MatchDto;
import hongik.ce.jolup.repository.CompetitionRepository;
import hongik.ce.jolup.repository.JoinRepository;
import hongik.ce.jolup.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MatchService {

    private final JoinRepository joinRepository;
    private final CompetitionRepository competitionRepository;
    private final MatchRepository matchRepository;

    @Transactional
    public Long saveMatch(Match match) {
        return matchRepository.save(match).getId();
    }

    @Transactional
    public void saveMatches(Long competitionId) {
        List<Member> members = joinRepository.findByCompetitionId(competitionId).stream()
                .map(Join::getMember).collect(Collectors.toList());
        Competition competition = competitionRepository.findById(competitionId).orElse(null);

        assert competition != null;
        if (competition.getCompetitionType().equals(CompetitionType.LEAGUE)) {
            Collections.shuffle(members);
            int count = members.size();
            if (count % 2 == 1) {
                for (int i = 0; i < count; i++) {
                    for (int j = 0; j < count/2; j++) {
                        Match match = Match.builder().competition(competition)
                                .home(members.get((i + j) % count))
                                .away(members.get((i + count - j - 2) % count))
                                .matchStatus(MatchStatus.READY)
                                .roundNo(i)
                                .matchNo(j)
                                .score(Score.builder().homeScore(0).awayScore(0).build())
                                .build();
                        matchRepository.save(match);
                    }
                }
            }
            else {
                int j;
                Member fixed = members.remove(0);
                for (int i = 0; i < count - 1; i++) {
                    for (j = 0; j < count/2 - 1; j++) {
                        Match match = Match.builder().competition(competition)
                                .home(members.get((i + j) % (count - 1)))
                                .away(members.get((i + count - j - 2) % (count - 1)))
                                .matchStatus(MatchStatus.READY)
                                .roundNo(i)
                                .matchNo(j)
                                .score(Score.builder().homeScore(0).awayScore(0).build())
                                .build();
                        matchRepository.save(match);
                    }
                    Match match = Match.builder().competition(competition)
                            .home(i % 2 == 0 ? members.get((i + j) % (count - 1)) : fixed)
                            .away(i % 2 == 0 ? fixed : members.get((i + j) % (count - 1)))
                            .matchStatus(MatchStatus.READY)
                            .roundNo(i)
                            .matchNo(j)
                            .score(Score.builder().homeScore(0).awayScore(0).build())
                            .build();
                    matchRepository.save(match);
                }
            }
        } else if (competition.getCompetitionType().equals(CompetitionType.TOURNAMENT)) {
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
                            .score(Score.builder().homeScore(0).awayScore(0).build()).build();
                    matchRepository.save(match);
                }
                match_num *= 2;
            }
        }
    }

    @Transactional
    public Long updateMatch(Long id) {
        Match match = matchRepository.findById(id).orElse(null);
        if (match == null) {
            return null;
        }
        return match.getId();
    }
    
    @Transactional
    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }

    public List<Match> findByCompetition(Long competitionId) {
        List<Match> matches = matchRepository.findByCompetitionId(competitionId);
        return matches;
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