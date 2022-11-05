package hongik.ce.jolup.module.match.application;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.match.endpoint.form.LocationForm;
import hongik.ce.jolup.module.match.endpoint.form.MatchForm;
import hongik.ce.jolup.module.match.endpoint.form.ScoreForm;
import hongik.ce.jolup.module.match.event.MatchUpdatedEvent;
import hongik.ce.jolup.module.match.infra.repository.MatchRepository;
import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final ApplicationEventPublisher publisher;

    public void update(Match match, MatchForm matchForm, Competition competition) {
        Participate home = competition.getParticipates().stream().filter(p -> p.getMember().equals(match.getHome())).findAny().orElse(null);
        Participate away = competition.getParticipates().stream().filter(p -> p.getMember().equals(match.getAway())).findAny().orElse(null);

        if (home != null && away != null) {
            checkMatchOldResult(competition, match,
                    Optional.ofNullable(matchForm.getHomeScore()).orElse(0),
                    Optional.ofNullable(matchForm.getAwayScore()).orElse(0),
                    home, away);

            changeNewMatchResult(competition, match, matchForm.getHomeScore(), matchForm.getAwayScore(), home, away);
        } else {
            matchForm.setHomeScore(null);
            matchForm.setAwayScore(null);
        }
        match.updateFrom(matchForm);
    }

    public void updateScore(Competition competition, Match match, ScoreForm scoreForm) {
        List<Member> members = competition.getParticipates().stream().map(Participate::getMember).collect(Collectors.toList());
        publisher.publishEvent(new MatchUpdatedEvent(match, "대회 '" + match.getCompetition().getTitle() + "'에서 경기 결과가 수정되었습니다.", members));
        match.updateScoreFrom(scoreForm);
    }

    private void changeNewMatchResult(Competition competition, Match match, Integer homeScore, Integer awayScore, Participate home, Participate away) {
        if (homeScore != null && awayScore != null) {
            if (homeScore > awayScore) {
                home.addWin(1);
                away.addLose(1);
                if (competition.isTournament()) {
                    setNextRoundMatch(competition, match, match.getHome());
                }
            } else if (homeScore < awayScore) {
                home.addLose(1);
                away.addWin(1);
                if (competition.isTournament()) {
                    setNextRoundMatch(competition, match, match.getAway());
                }
            } else {
                home.addDraw(1);
                away.addDraw(1);
            }
            home.addGoalFor(homeScore);
            home.addGoalAgainst(awayScore);
            away.addGoalFor(awayScore);
            away.addGoalAgainst(homeScore);
        }
    }

    private void checkMatchOldResult(Competition competition, Match match, Integer homeScore, Integer awayScore, Participate home, Participate away) {
        if (match.isFinished()) {
            if (match.getHomeScore() > match.getAwayScore()) {
                home.subWin(1);
                away.subLose(1);
                if (competition.isTournament() && homeScore <= awayScore) {
                    resetNextRoundMatch(competition, match);
                }
            } else if (match.getHomeScore() < match.getAwayScore()) {
                home.subLose(1);
                away.subWin(1);
                if (competition.isTournament() && homeScore >= awayScore) {
                    resetNextRoundMatch(competition, match);
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

    private void resetNextRoundMatch(Competition competition, Match match) {
        Match nextMatch = matchRepository
                .findByCompetitionAndRoundAndNumber(competition, match.getRound() - 1, match.getNumber() / 2)
                .orElse(null);

        if (nextMatch != null) {
            if (nextMatch.isFinished()) {
                Participate home = competition.getParticipates().stream().filter(p -> p.getMember().equals(nextMatch.getHome())).findAny().orElseThrow(() -> new IllegalStateException("존재하지 않는 참가자입니다."));
                Participate away = competition.getParticipates().stream().filter(p -> p.getMember().equals(nextMatch.getAway())).findAny().orElseThrow(() -> new IllegalStateException("존재하지 않는 참가자입니다."));
                checkMatchOldResult(competition, nextMatch, 0, 0, home, away);
            }
            if (match.getNumber() % 2 == 0) {
                nextMatch.updateHome(null);
            } else {
                nextMatch.updateAway(null);
            }
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

    public void updateStartDateTime(Match match, LocalDateTime newStartDateTime) {
        match.updateStartDateTime(newStartDateTime);
    }

    public void updateLocation(Match match, LocationForm locationForm) {
        match.updateLocation(locationForm);
    }
}
