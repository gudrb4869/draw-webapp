package hongik.ce.jolup.module.match.application;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.match.endpoint.form.ScoreForm;
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

    public void updateScore(Competition competition, Match match, ScoreForm scoreForm) {
        Participate home = competition.getParticipates().stream().filter(p -> p.getMember().equals(match.getHome())).findAny().orElseThrow(() -> new IllegalStateException("존재하지 않는 참가자입니다."));
        Participate away = competition.getParticipates().stream().filter(p -> p.getMember().equals(match.getAway())).findAny().orElseThrow(() -> new IllegalStateException("존재하지 않는 참가자입니다."));

        if (!scoreForm.isStatus()) {
            scoreForm.setHomeScore(0);
            scoreForm.setAwayScore(0);
        }
        // 기존 경기 결과 확인 후 초기화
        checkMatchOldResult(competition, match, scoreForm, home, away);

        // 새로운 경기 수정 결과 반영
        updateNewMatchResult(competition, match, scoreForm, home, away);

        List<Member> members = competition.getParticipates().stream().map(Participate::getMember).collect(Collectors.toList());
        publisher.publishEvent(new MatchUpdatedEvent(match, "대회 '" + match.getCompetition().getTitle() + "'에서 경기 결과가 수정되었습니다.", members));
        match.updateScoreFrom(scoreForm);
    }

    private void updateNewMatchResult(Competition competition, Match match, ScoreForm scoreForm, Participate home, Participate away) {
        if (scoreForm.isStatus()) {
            if (scoreForm.getHomeScore() > scoreForm.getAwayScore()) {
                home.addWin(1);
                away.addLose(1);
                if (competition.isTournament()) {
                    setNextRoundMatch(competition, match, match.getHome());
                }
            } else if (scoreForm.getHomeScore() < scoreForm.getAwayScore()) {
                home.addLose(1);
                away.addWin(1);
                if (competition.isTournament()) {
                    setNextRoundMatch(competition, match, match.getAway());
                }
            } else {
                home.addDraw(1);
                away.addDraw(1);
            }
            home.addGoalFor(scoreForm.getHomeScore());
            home.addGoalAgainst(scoreForm.getAwayScore());
            away.addGoalFor(scoreForm.getAwayScore());
            away.addGoalAgainst(scoreForm.getHomeScore());
        }
    }

    private void checkMatchOldResult(Competition competition, Match match, ScoreForm scoreForm, Participate home, Participate away) {
        if (match.isStatus()) {
            if (match.getHomeScore() > match.getAwayScore()) {
                home.subWin(1);
                away.subLose(1);
                if (competition.isTournament() && scoreForm.getHomeScore() <= scoreForm.getAwayScore()) {
                    resetNextRoundMatch(competition, match);
                }
            } else if (match.getHomeScore() < match.getAwayScore()) {
                home.subLose(1);
                away.subWin(1);
                if (competition.isTournament() && scoreForm.getHomeScore() >= scoreForm.getAwayScore()) {
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
            if (nextMatch.isStatus()) {
                Participate home = competition.getParticipates().stream().filter(p -> p.getMember().equals(nextMatch.getHome())).findAny().orElseThrow(() -> new IllegalStateException("존재하지 않는 참가자입니다."));
                Participate away = competition.getParticipates().stream().filter(p -> p.getMember().equals(nextMatch.getAway())).findAny().orElseThrow(() -> new IllegalStateException("존재하지 않는 참가자입니다."));
                if (nextMatch.getHomeScore() > nextMatch.getAwayScore()) {
                    home.subWin(1);
                    away.subLose(1);
                } else if (nextMatch.getHomeScore() < nextMatch.getAwayScore()) {
                    home.subLose(1);
                    away.subWin(1);
                } else {
                    home.subDraw(1);
                    away.subDraw(1);
                }
                home.subGoalFor(nextMatch.getHomeScore());
                home.subGoalAgainst(nextMatch.getAwayScore());
                away.subGoalFor(nextMatch.getAwayScore());
                away.subGoalAgainst(nextMatch.getHomeScore());

                resetNextRoundMatch(competition, nextMatch);
            }
            nextMatch.reset();
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
        if (match.isClosed()) {
            throw new IllegalArgumentException("접근할수 없는 경기입니다.");
        }
    }
}
