package hongik.ce.jolup.module.match.application;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.LeagueTable;
import hongik.ce.jolup.module.match.domain.entity.Status;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.match.event.MatchUpdatedEvent;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.competition.infra.repository.CompetitionRepository;
import hongik.ce.jolup.module.match.infra.repository.MatchRepository;
import hongik.ce.jolup.module.competition.infra.repository.LeagueTableRepository;
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
public class LeagueService {

    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;
    private final MatchRepository matchRepository;
    private final LeagueTableRepository leagueTableRepository;
    private final ApplicationEventPublisher eventPublisher;


    public void createMatches(List<Member> memberList, Competition competition) {
        Collections.shuffle(memberList);
        Set<Match> matches = new HashSet<>();
        int count = memberList.size();
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
            /*if (option.equals(CompetitionOption.DOUBLE)) {
                for (int i = 0; i < count; i++) {
                    for (int j = 0; j < count/2; j++) {
                        Match Match = Match.builder().league(league)
                                .home(i % 2 == 0 ? memberList.get((i + count - j - 2) % count) : memberList.get((i + j) % count))
                                .away(i % 2 == 0 ? memberList.get((i + j) % count) : memberList.get((i + count - j - 2) % count))
                                .status(Status.BEFORE)
                                .round(count + i)
                                .homeScore(0).awayScore(0)
                                .build();
                        MatchRepository.save(Match);
                    }
                }
            }*/
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
            /*if (option.equals(CompetitionOption.DOUBLE)) {
                for (int i = 0; i < count - 1; i++) {
                    for (j = 0; j < count/2 - 1; j++) {
                        Match match = Match.builder().league(league)
                                .home(memberList.get(i % 2 == 0 ? (i + count - j - 2) % (count - 1) : (i + j) % (count - 1)))
                                .away(memberList.get(i % 2 == 0 ? (i + j) % (count - 1) : (i + count - j - 2) % (count - 1)))
                                .status(Status.BEFORE)
                                .round(count - 1 + i)
                                .homeScore(0).awayScore(0)
                                .build();
                        MatchRepository.save(match);
                    }
                    Match match = Match.builder().league(league)
                            .home(i % 2 == 0 ? fixed : memberList.get((i + j) % (count - 1)))
                            .away(i % 2 == 0 ? memberList.get((i + j) % (count - 1)) : fixed)
                            .status(Status.BEFORE)
                            .round(count - 1 + i)
                            .homeScore(0).awayScore(0)
                            .build();
                    MatchRepository.save(match);
                }
            }*/
        }
        matchRepository.saveAll(matches);
    }

    public void save(List<Long> memberIds, Long competitionId) {
        List<Member> members = memberRepository.findAllById(memberIds);
        Competition competition = competitionRepository.findById(competitionId).orElse(null);

        assert competition != null;
        Collections.shuffle(members);
        int count = members.size();
        if (count % 2 == 1) {
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < count/2; j++) {
                    Match match = Match.builder().competition(competition)
                            .home(i % 2 == 0 ? members.get((i + j) % count) : members.get((i + count - j - 2) % count))
                            .away(i % 2 == 0 ? members.get((i + count - j - 2) % count) : members.get((i + j) % count))
                            .status(Status.BEFORE)
                            .round(i)
                            .number(j)
                            .homeScore(0).awayScore(0)
                            .build();
                    matchRepository.save(match);
                }
            }
            /*if (option.equals(CompetitionOption.DOUBLE)) {
                for (int i = 0; i < count; i++) {
                    for (int j = 0; j < count/2; j++) {
                        Match Match = Match.builder().league(league)
                                .home(i % 2 == 0 ? members.get((i + count - j - 2) % count) : members.get((i + j) % count))
                                .away(i % 2 == 0 ? members.get((i + j) % count) : members.get((i + count - j - 2) % count))
                                .status(Status.BEFORE)
                                .round(count + i)
                                .homeScore(0).awayScore(0)
                                .build();
                        MatchRepository.save(Match);
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
                    Match match = Match.builder().competition(competition)
                            .home(members.get(i % 2 == 0 ? (i + j) % (count - 1) : (i + count - j - 2) % (count - 1)))
                            .away(members.get(i % 2 == 0 ? (i + count - j - 2) % (count - 1) : (i + j) % (count - 1)))
                            .status(Status.BEFORE)
                            .round(i)
                            .number(j)
                            .homeScore(0).awayScore(0)
                            .build();
                    matchRepository.save(match);
                }
                Match match = Match.builder().competition(competition)
                        .home(i % 2 == 0 ? members.get((i + j) % (count - 1)) : fixed)
                        .away(i % 2 == 0 ? fixed : members.get((i + j) % (count - 1)))
                        .status(Status.BEFORE)
                        .round(i)
                        .number(j)
                        .homeScore(0).awayScore(0)
                        .build();
                matchRepository.save(match);
            }
            /*if (option.equals(CompetitionOption.DOUBLE)) {
                for (int i = 0; i < count - 1; i++) {
                    for (j = 0; j < count/2 - 1; j++) {
                        Match match = Match.builder().league(league)
                                .home(members.get(i % 2 == 0 ? (i + count - j - 2) % (count - 1) : (i + j) % (count - 1)))
                                .away(members.get(i % 2 == 0 ? (i + j) % (count - 1) : (i + count - j - 2) % (count - 1)))
                                .status(Status.BEFORE)
                                .round(count - 1 + i)
                                .homeScore(0).awayScore(0)
                                .build();
                        MatchRepository.save(match);
                    }
                    Match match = Match.builder().league(league)
                            .home(i % 2 == 0 ? fixed : members.get((i + j) % (count - 1)))
                            .away(i % 2 == 0 ? members.get((i + j) % (count - 1)) : fixed)
                            .status(Status.BEFORE)
                            .round(count - 1 + i)
                            .homeScore(0).awayScore(0)
                            .build();
                    MatchRepository.save(match);
                }
            }*/
        }
    }

    public void update(Long matchId, Long competitionId, Status status, Integer homeScore, Integer awayScore, Long homeId, Long awayId) {
        Match match = matchRepository.findById(matchId).orElse(null);
        LeagueTable home = leagueTableRepository.findByMemberIdAndCompetitionId(homeId, competitionId).orElse(null);
        LeagueTable away = leagueTableRepository.findByMemberIdAndCompetitionId(awayId, competitionId).orElse(null);
        if (match == null || home == null || away == null) {
            return;
        }

        if (match.getStatus().equals(Status.AFTER)) {
            if (match.getHomeScore() > match.getAwayScore()) {
                home.subWin(1);
                away.subLose(1);
            } else if (match.getHomeScore() < match.getAwayScore()) {
                home.subLose(1);
                away.subWin(1);
            } else {
                home.subDraw(1);
                away.subDraw(1);
            }
            home.subGoalFor(match.getHomeScore());
            home.subGoalAgainst(match.getAwayScore());
            away.subGoalFor(match.getAwayScore());
            away.subGoalAgainst(match.getHomeScore());
        }

        if (status.equals(Status.AFTER)) {
            if (homeScore > awayScore) {
                home.addWin(1);
                away.addLose(1);
            } else if (homeScore < awayScore) {
                home.addLose(1);
                away.addWin(1);
            } else {
                home.addDraw(1);
                away.addDraw(1);
            }
            home.addGoalFor(homeScore);
            home.addGoalAgainst(awayScore);
            away.addGoalFor(awayScore);
            away.addGoalAgainst(homeScore);
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

    public void setNull(Long userId) {
        List<Match> homeMatchs = matchRepository.findByHomeId(userId);
        for (Match Match : homeMatchs) {
            Match.updateHome(null);
        }
        List<Match> awayMatchs = matchRepository.findByAwayId(userId);
        for (Match Match : awayMatchs) {
            Match.updateAway(null);
        }
    }

    public Page<Match> findByCompetitionId(Long competitionId, int count, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        return matchRepository.findByCompetitionId(competitionId, PageRequest.of(page, count / 2));
    }

    public List<Match> findByCompetitionId(Long competitionId) {
        return matchRepository.findByCompetitionId(competitionId);
    }

    public Match findByIdAndCompetitionId(Long id, Long competitionId) {
        return matchRepository.findByIdAndCompetitionId(id, competitionId).orElse(null);
    }

    public Match findOne(Long MatchId) {
        return matchRepository.findById(MatchId).orElse(null);
    }

    public Match findOne(Long competitionId, Integer round) {
        return matchRepository.findByCompetitionIdAndRound(competitionId, round).orElse(null);
    }
}