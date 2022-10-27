package hongik.ce.jolup.module.match.infra.repository;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findByCompetitionAndRoundAndNumber(Competition competition, Integer round, Integer match);

    @EntityGraph(attributePaths = {"competition", "home", "away"})
    Page<Match> findMatchWithAllByCompetition(Competition competition, Pageable pageable);

    @EntityGraph(attributePaths = {"competition", "home", "away"})
    List<Match> findMatchWithAllByCompetition(Competition competition);

    @EntityGraph(value = "Match.withCompetitionAndRoomAndHomeAndAway")
    Optional<Match> findMatchById(Long matchId);

    List<Match> findMatchByHome(Member member);

    List<Match> findMatchByAway(Member member);
}
