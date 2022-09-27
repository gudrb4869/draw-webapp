package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.LeagueGame;
import hongik.ce.jolup.domain.competition.Status;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.repository.CompetitionRepository;
import hongik.ce.jolup.repository.LeagueGameRepository;
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
public class LeagueGameService {

    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;
    private final LeagueGameRepository leagueGameRepository;

    @Transactional
    public void save(List<Long> memberIds, Long competitionId) {
        List<Member> members = memberRepository.findAllById(memberIds);
        Competition competition = competitionRepository.findById(competitionId).orElse(null);

        assert competition != null;
        Collections.shuffle(members);
        int count = members.size();
        if (count % 2 == 1) {
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < count/2; j++) {
                    LeagueGame leagueGame = LeagueGame.builder().competition(competition)
                            .home(i % 2 == 0 ? members.get((i + j) % count) : members.get((i + count - j - 2) % count))
                            .away(i % 2 == 0 ? members.get((i + count - j - 2) % count) : members.get((i + j) % count))
                            .status(Status.BEFORE)
                            .round(i)
                            .homeScore(0).awayScore(0)
                            .build();
                    leagueGameRepository.save(leagueGame);
                }
            }
            /*if (option.equals(CompetitionOption.DOUBLE)) {
                for (int i = 0; i < count; i++) {
                    for (int j = 0; j < count/2; j++) {
                        LeagueGame leagueGame = LeagueGame.builder().league(league)
                                .home(i % 2 == 0 ? members.get((i + count - j - 2) % count) : members.get((i + j) % count))
                                .away(i % 2 == 0 ? members.get((i + j) % count) : members.get((i + count - j - 2) % count))
                                .status(Status.BEFORE)
                                .round(count + i)
                                .homeScore(0).awayScore(0)
                                .build();
                        leagueGameRepository.save(leagueGame);
                    }
                }
            }*/
        }
        else {
            int j;
            Random random = new Random();
            Member fixed = members.remove(random.nextInt(count));
            for (int i = 0; i < count - 1; i++) {
                for (j = 0; j < count/2 - 1; j++) {
                    LeagueGame leagueGame = LeagueGame.builder().competition(competition)
                            .home(members.get(i % 2 == 0 ? (i + j) % (count - 1) : (i + count - j - 2) % (count - 1)))
                            .away(members.get(i % 2 == 0 ? (i + count - j - 2) % (count - 1) : (i + j) % (count - 1)))
                            .status(Status.BEFORE)
                            .round(i)
                            .homeScore(0).awayScore(0)
                            .build();
                    leagueGameRepository.save(leagueGame);
                }
                LeagueGame match = LeagueGame.builder().competition(competition)
                        .home(i % 2 == 0 ? members.get((i + j) % (count - 1)) : fixed)
                        .away(i % 2 == 0 ? fixed : members.get((i + j) % (count - 1)))
                        .status(Status.BEFORE)
                        .round(i)
                        .homeScore(0).awayScore(0)
                        .build();
                leagueGameRepository.save(match);
            }
            /*if (option.equals(CompetitionOption.DOUBLE)) {
                for (int i = 0; i < count - 1; i++) {
                    for (j = 0; j < count/2 - 1; j++) {
                        LeagueGame match = LeagueGame.builder().league(league)
                                .home(members.get(i % 2 == 0 ? (i + count - j - 2) % (count - 1) : (i + j) % (count - 1)))
                                .away(members.get(i % 2 == 0 ? (i + j) % (count - 1) : (i + count - j - 2) % (count - 1)))
                                .status(Status.BEFORE)
                                .round(count - 1 + i)
                                .homeScore(0).awayScore(0)
                                .build();
                        leagueGameRepository.save(match);
                    }
                    LeagueGame match = LeagueGame.builder().league(league)
                            .home(i % 2 == 0 ? fixed : members.get((i + j) % (count - 1)))
                            .away(i % 2 == 0 ? members.get((i + j) % (count - 1)) : fixed)
                            .status(Status.BEFORE)
                            .round(count - 1 + i)
                            .homeScore(0).awayScore(0)
                            .build();
                    leagueGameRepository.save(match);
                }
            }*/
        }
    }

    @Transactional
    public void update(Long matchId, Status status, Integer homeScore, Integer awayScore) {
        LeagueGame leagueGame = leagueGameRepository.findById(matchId).orElse(null);
        if (leagueGame == null) {
            return;
        }
        leagueGame.updateStatus(status);
        leagueGame.updateScore(homeScore, awayScore);
    }
    
    @Transactional
    public void delete(Long id) {
        leagueGameRepository.deleteById(id);
    }

    @Transactional
    public void setNull(Long userId) {
        List<LeagueGame> homeLeagueGames = leagueGameRepository.findByHomeId(userId);
        for (LeagueGame leagueGame : homeLeagueGames) {
            leagueGame.updateHome(null);
        }
        List<LeagueGame> awayLeagueGames = leagueGameRepository.findByAwayId(userId);
        for (LeagueGame leagueGame : awayLeagueGames) {
            leagueGame.updateAway(null);
        }
    }

    public Page<LeagueGame> findByCompetitionId(Long competitionId, int count, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        return leagueGameRepository.findByCompetitionId(competitionId, PageRequest.of(page, count / 2));
    }

    public List<LeagueGame> findByCompetitionId(Long competitionId) {
        return leagueGameRepository.findByCompetitionId(competitionId);
    }

    public LeagueGame findByIdAndCompetitionId(Long id, Long competitionId) {
        return leagueGameRepository.findByIdAndCompetitionId(id, competitionId).orElse(null);
    }

    public LeagueGame findOne(Long leagueGameId) {
        return leagueGameRepository.findById(leagueGameId).orElse(null);
    }

    public LeagueGame findOne(Long competitionId, Integer round) {
        return leagueGameRepository.findByCompetitionIdAndRound(competitionId, round).orElse(null);
    }
}