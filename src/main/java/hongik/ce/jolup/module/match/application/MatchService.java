package hongik.ce.jolup.module.match.application;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.match.endpoint.form.DateForm;
import hongik.ce.jolup.module.match.endpoint.form.LocationForm;
import hongik.ce.jolup.module.match.endpoint.form.MatchForm;
import hongik.ce.jolup.module.match.endpoint.form.ScoreForm;
import hongik.ce.jolup.module.match.event.MatchUpdatedEvent;
import hongik.ce.jolup.module.match.infra.repository.MatchRepository;
import hongik.ce.jolup.module.account.domain.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final ApplicationEventPublisher publisher;

    public void updateScore(Match match, ScoreForm scoreForm, Competition competition) {
        if (!scoreForm.isFinished()) {
            scoreForm.setHomeScore(0);
            scoreForm.setAwayScore(0);
        }

        // (1) 이미 반영된 경기 결과가 있는지 체크 후 있으면 랭킹에서 경기 결과 제외
        checkExistsMatchResult(match, competition);

        // (2) 경기 결과 수정
        match.updateScoreFrom(scoreForm);

        // (3) 수정된 경기 결과 여부에 따라 랭킹에 반영
        updateMatchResult(match, competition);

        checkTournamentNextMatchExistsMatchResult(match, competition);

        List<Account> accounts = competition.getParticipates().stream().map(Participate::getAccount).collect(Collectors.toList());
        publisher.publishEvent(new MatchUpdatedEvent(match, "대회 '" + match.getCompetition().getTitle() + "'에서 경기 결과가 수정되었습니다.", accounts));
    }

    public void updateDate(Match match, DateForm dateForm) {
        match.updateStartDateTime(dateForm.getStartDateTime());
    }

    private void checkTournamentNextMatchExistsMatchResult(Match match, Competition competition) {
        if (competition.isTournament()) {
            if (match.getHomeScore() > match.getAwayScore()) {
                setNextRoundMatch(competition, match, match.getHome());
            } else if (match.getHomeScore() < match.getAwayScore()) {
                setNextRoundMatch(competition, match, match.getAway());
            } else {
                setNextRoundMatch(competition, match, null);
            }
        }
    }

    private void updateMatchResult(Match match, Competition competition) {
        if (match.isFinished()) {
            Participate home = competition.getParticipates().stream().filter(p -> p.getAccount().equals(match.getHome())).findFirst().orElseThrow(() -> new IllegalStateException("홈팀이 존재하지 않습니다."));
            Participate away = competition.getParticipates().stream().filter(p -> p.getAccount().equals(match.getAway())).findFirst().orElseThrow(() -> new IllegalStateException("어웨이팀이 존재하지 않습니다."));
            if (match.getHomeScore() > match.getAwayScore()) {
                home.addWin();
                away.addLose();
            } else if (match.getHomeScore() < match.getAwayScore()) {
                home.addLose();
                away.addWin();
            } else {
                home.addDraw();
                away.addDraw();
            }
            home.addGoalFor(match.getHomeScore());
            home.addGoalAgainst(match.getAwayScore());
            away.addGoalFor(match.getAwayScore());
            away.addGoalAgainst(match.getHomeScore());
        }
    }

    private void checkExistsMatchResult(Match match, Competition competition) {
        if (match.isFinished()) {
            Participate home = competition.getParticipates().stream().filter(p -> p.getAccount().equals(match.getHome())).findFirst().orElseThrow(() -> new IllegalStateException("홈팀이 존재하지 않습니다."));
            Participate away = competition.getParticipates().stream().filter(p -> p.getAccount().equals(match.getAway())).findFirst().orElseThrow(() -> new IllegalStateException("어웨이팀이 존재하지 않습니다."));
            if (match.getHomeScore() > match.getAwayScore()) {
                home.subWin();
                away.subLose();
            } else if (match.getHomeScore() < match.getAwayScore()) {
                home.subLose();
                away.subWin();
            } else {
                home.subDraw();
                away.subDraw();
            }
            home.subGoalFor(match.getHomeScore());
            home.subGoalAgainst(match.getAwayScore());
            away.subGoalFor(match.getAwayScore());
            away.subGoalAgainst(match.getHomeScore());
        }
    }

    private void setNextRoundMatch(Competition competition, Match match, Account account) {
        Match nextMatch = matchRepository
                .findByCompetitionAndRoundAndNumber(competition, match.getRound() - 1, match.getNumber() / 2)
                .orElse(null);
        if (nextMatch != null) {
            checkExistsMatchResult(nextMatch, competition);
            if (match.getNumber() % 2 == 0) {
                nextMatch.updateHome(account);
            } else {
                nextMatch.updateAway(account);
            }
            if (account == null) {
                nextMatch.reset();
            }
            updateMatchResult(nextMatch, competition);
            checkTournamentNextMatchExistsMatchResult(nextMatch, competition);
        }
    }

    public void update(Match match, MatchForm matchForm, Competition competition) {
//        match.updateFrom(matchForm);
    }

    public Match getMatch(Competition competition, Long matchId) {
        Match match = matchRepository.findMatchById(matchId).orElse(null);
        check(competition, match);
        return match;
    }

    private void check(Competition competition, Match match) {
        if (match == null || !match.getCompetition().equals(competition) || match.isClosed()) {
            throw new IllegalArgumentException("존재하지 않는 경기입니다.");
        }
    }

    public void updateStartDateTime(Match match, LocalDateTime startDateTime) {
        match.updateStartDateTime(startDateTime);
    }

    public void updateLocation(Match match, LocationForm locationForm) {
        match.updateLocation(locationForm);
    }
}
