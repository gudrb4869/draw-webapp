package hongik.ce.jolup.module.match.application;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.match.domain.entity.Status;
import hongik.ce.jolup.module.match.endpoint.form.MatchForm;
import hongik.ce.jolup.module.match.event.MatchUpdatedEvent;
import hongik.ce.jolup.module.match.infra.repository.MatchRepository;
import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final ApplicationEventPublisher publisher;

    public void updateMatch(Competition competition, Match match, MatchForm matchForm) {
        Participate home = competition.getParticipates().stream().filter(p -> p.getMember().equals(match.getHome())).findAny().orElseThrow(() -> new IllegalStateException("존재하지 않는 참가자입니다."));
        Participate away = competition.getParticipates().stream().filter(p -> p.getMember().equals(match.getAway())).findAny().orElseThrow(() -> new IllegalStateException("존재하지 않는 참가자입니다."));

        if (matchForm.getStatus().equals(Status.BEFORE)) {
            matchForm.setHomeScore(null);
            matchForm.setAwayScore(null);
        }
        if (match.getStatus().equals(Status.AFTER)) {
            if (match.getHomeScore() > match.getAwayScore()) {
                home.subWin(1);
                away.subLose(1);
                if (competition.isTournament() && matchForm.getHomeScore() <= matchForm.getAwayScore()) {
                    resetMatches(competition, match);
                }
            } else if (match.getHomeScore() < match.getAwayScore()) {
                home.subLose(1);
                away.subWin(1);
                if (competition.isTournament() && matchForm.getHomeScore() >= matchForm.getAwayScore()) {
                    resetMatches(competition, match);
                }
            } else {
                home.subDraw(1);
                away.subDraw(1);
            }
            home.subGoalFor(match.getHomeScore());
            home.subGoalAgainst(match.getAwayScore());
            away.subGoalFor(match.getAwayScore());
            away.subGoalAgainst(match.getHomeScore());
        }

        if (matchForm.getStatus().equals(Status.AFTER)) {
            if (matchForm.getHomeScore() > matchForm.getAwayScore()) {
                home.addWin(1);
                away.addLose(1);
                if (competition.isTournament()) {
                    setNextRoundMatch(competition, match, match.getHome());
                }
            } else if (matchForm.getHomeScore() < matchForm.getAwayScore()) {
                home.addLose(1);
                away.addWin(1);
                if (competition.isTournament()) {
                    setNextRoundMatch(competition, match, match.getAway());
                }
            } else {
                home.addDraw(1);
                away.addDraw(1);
            }
            home.addGoalFor(matchForm.getHomeScore());
            home.addGoalAgainst(matchForm.getAwayScore());
            away.addGoalFor(matchForm.getAwayScore());
            away.addGoalAgainst(matchForm.getHomeScore());
        }

        List<Member> members = competition.getParticipates().stream().map(Participate::getMember).collect(Collectors.toList());
        publisher.publishEvent(new MatchUpdatedEvent(match, "대회 '" + match.getCompetition().getTitle() + "'에서 경기 결과가 수정되었습니다.", members));
        match.update(matchForm);
    }

    private void setNextRoundMatch(Competition competition, Match match, Member member) {
        Match nextMatch = matchRepository
                .findByCompetitionAndRoundAndNumber(competition, match.getRound() - 1, match.getNumber() / 2)
                .orElse(null);
        if (nextMatch != null) {
            if (match.getNumber() % 2 == 0) {
                nextMatch.updateHome(member);
            } else {
                nextMatch.updateAway(member);
            }
        }
    }

    private void resetMatches(Competition competition, Match match) {
        Match cur = match;
        while (true) {
            Match next = matchRepository
                    .findByCompetitionAndRoundAndNumber(competition, cur.getRound() - 1, cur.getNumber() / 2)
                    .orElse(null);
            if (next == null) {
                break;
            }
            if (cur.getNumber() % 2 == 0) {
                next.resetHome();
            } else {
                next.resetAway();
            }
            next.reset();
            cur = next;
        }
    }

    public Match getMatch(Competition competition, Long matchId) {
        Match match = matchRepository.findMatchById(matchId).orElse(null);
        check(competition, match);
        return match;
    }

    private void check(Competition competition, Match match) {
        if (match == null || !match.getCompetition().equals(competition)) {
            throw new IllegalArgumentException("존재하지 않는 경기입니다.");
        }
    }
}
