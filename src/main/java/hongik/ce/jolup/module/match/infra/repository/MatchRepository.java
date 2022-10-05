package hongik.ce.jolup.module.match.infra.repository;

import hongik.ce.jolup.module.match.domain.entity.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("select m from Match m join fetch m.competition" +
            " left join fetch m.home left join fetch m.away" +
            " where m.competition.id = :competitionId" +
            " order by m.round asc, m.number asc")
    List<Match> findByCompetitionId(@Param("competitionId") Long competitionId);

    Optional<Match> findByCompetitionIdAndRound(Long competitionId, Integer round);

    Optional<Match> findByCompetitionIdAndRoundAndNumber(Long competitionId, Integer round, Integer match);

    @Query("select m from Match m join fetch m.competition" +
            " left join fetch m.home left join fetch m.away" +
            " where m.id = :id and m.competition.id = :competitionId")
    Optional<Match> findByIdAndCompetitionId(@Param("id") Long id, @Param("competitionId") Long competitionId);

    @Query(value = "select m from Match m join fetch m.competition" +
            " left join fetch m.home left join fetch m.away" +
            " where m.competition.id = :competitionId" +
            " order by m.round asc, m.number asc",
            countQuery = "select count(m) from Match m where m.competition.id = :competitionId")
    Page<Match> findByCompetitionId(@Param("competitionId") Long competitionId, Pageable pageable);

    @Query("select m from Match m join fetch m.competition" +
            " left join fetch m.home left join fetch m.away" +
            " where m.home.id = :homeId")
    List<Match> findByHomeId(@Param("homeId") Long homeId);

    @Query("select m from Match m join fetch m.competition" +
            " left join fetch m.home left join fetch m.away" +
            " where m.away.id = :awayId")
    List<Match> findByAwayId(@Param("awayId") Long awayId);
}
