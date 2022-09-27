package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.competition.LeagueGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeagueGameRepository extends JpaRepository<LeagueGame, Long> {

    @Query("select l from LeagueGame l join fetch l.competition" +
            " left join fetch l.home left join fetch l.away" +
            " where l.competition.id = :competitionId" +
            " order by l.round asc")
    List<LeagueGame> findByCompetitionId(@Param("competitionId") Long competitionId);

    Optional<LeagueGame> findByCompetitionIdAndRound(Long competitionId, Integer round);

    @Query("select l from LeagueGame l join fetch l.competition" +
            " left join fetch l.home left join fetch l.away" +
            " where l.id = :id and l.competition.id = :competitionId")
    Optional<LeagueGame> findByIdAndCompetitionId(@Param("id") Long id, @Param("competitionId") Long competitionId);

    @Query(value = "select l from LeagueGame l join fetch l.competition" +
            " left join fetch l.home left join fetch l.away" +
            " where l.competition.id = :competitionId" +
            " order by l.round asc",
            countQuery = "select count(l) from LeagueGame l where l.competition.id = :competitionId")
    Page<LeagueGame> findByCompetitionId(@Param("competitionId") Long competitionId, Pageable pageable);

    @Query("select l from LeagueGame l join fetch l.competition" +
            " left join fetch l.home left join fetch l.away" +
            " where l.home.id = :homeId")
    List<LeagueGame> findByHomeId(@Param("homeId") Long homeId);

    @Query("select l from LeagueGame l join fetch l.competition" +
            " left join fetch l.home left join fetch l.away" +
            " where l.away.id = :awayId")
    List<LeagueGame> findByAwayId(@Param("awayId") Long awayId);
}
