package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.Status;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.competition.SingleLegGame;
import hongik.ce.jolup.repository.CompetitionRepository;
import hongik.ce.jolup.repository.MemberRepository;
import hongik.ce.jolup.repository.SingleLegGameRepository;
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
public class SingleLegGameService {

    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;
    private final SingleLegGameRepository singleLegGameRepository;

    @Transactional
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
                SingleLegGame singleLegGame = SingleLegGame.builder().competition(competition)
                        .home(i == round - 1 ? members.remove(0) :
                                (i == round - 2 && auto_win_num > 0 && list.contains(j * 2) ? members.remove(0) : null))
                        .away(i == round - 1 ? members.remove(0) :
                                (i == round - 2 && auto_win_num > 0 && list.contains(j * 2 + 1) ? members.remove(0) : null))
                        .status(Status.BEFORE)
                        .round(i)
                        .number(j)
                        .homeScore(0).awayScore(0)
                        .build();
                singleLegGameRepository.save(singleLegGame);
            }
            number *= 2;
        }
    }

    @Transactional
    public void update(Long id, Long competitionId, Status status, Integer homeScore, Integer awayScore) {
        SingleLegGame singleLegGame = singleLegGameRepository.findById(id).orElse(null);
        if (singleLegGame == null) {
            return;
        }

        if (singleLegGame.getStatus().equals(Status.AFTER)) {
            if (singleLegGame.getHomeScore() > singleLegGame.getAwayScore() && homeScore <= awayScore) {
                resetSingleLegGames(competitionId, singleLegGame);
            }
            else if (singleLegGame.getHomeScore() < singleLegGame.getAwayScore() && homeScore >= awayScore) {
                resetSingleLegGames(competitionId, singleLegGame);
            }
        }
        if (status.equals(Status.AFTER)) {
            if (homeScore > awayScore) {
                setNextRoundSingleLegGame(competitionId, singleLegGame, singleLegGame.getHome());
            } else if (homeScore < awayScore) {
                setNextRoundSingleLegGame(competitionId, singleLegGame, singleLegGame.getAway());
            }
        }

        singleLegGame.updateStatus(status);
        singleLegGame.updateScore(homeScore, awayScore);
    }
    
    @Transactional
    public void delete(Long id) {
        singleLegGameRepository.deleteById(id);
    }

    @Transactional
    public void setNull(Long memberId) {
        List<SingleLegGame> homeMatches = singleLegGameRepository.findByHomeId(memberId);
        for (SingleLegGame match : homeMatches) {
            match.updateHome(null);
        }
        List<SingleLegGame> awayMatches = singleLegGameRepository.findByAwayId(memberId);
        for (SingleLegGame match : awayMatches) {
            match.updateAway(null);
        }
    }

    public Page<SingleLegGame> findByCompetition(Long competitionId, int count, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        return singleLegGameRepository.findByCompetitionId(competitionId, PageRequest.of(page, count / 2));
    }

    public List<SingleLegGame> findByCompetition(Long competitionId) {
        return singleLegGameRepository.findByCompetitionId(competitionId);
    }

    public SingleLegGame findByIdAndCompetitionId(Long id, Long competitionId) {
        return singleLegGameRepository.findByIdAndCompetitionId(id, competitionId).orElse(null);
    }

    public SingleLegGame findOne(Long id) {
        return singleLegGameRepository.findById(id).orElse(null);
    }

    public SingleLegGame findOne(Long competitionId, Integer round, Integer number) {
        return singleLegGameRepository.findByCompetitionIdAndRoundAndNumber(competitionId, round, number).orElse(null);
    }

    private void setNextRoundSingleLegGame(Long competitionId, SingleLegGame singleLegGame, Member member) {
        SingleLegGame nextMatch = singleLegGameRepository
                .findByCompetitionIdAndRoundAndNumber(competitionId, singleLegGame.getRound() - 1, singleLegGame.getNumber() / 2)
                .orElse(null);
        if (nextMatch != null) {
            if (singleLegGame.getNumber() % 2 == 0) {
                nextMatch.updateHome(member);
            } else {
                nextMatch.updateAway(member);
            }
        }
    }

    private void resetSingleLegGames(Long competitionId, SingleLegGame singleLegGame) {
        SingleLegGame cur = singleLegGame;
        while (true) {
            SingleLegGame next = singleLegGameRepository
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