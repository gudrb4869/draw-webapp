package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.competition.SingleLegGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SingleLegGameRepository extends JpaRepository<SingleLegGame, Long> {

    @Query("select s from SingleLegGame s join fetch s.competition" +
            " left join fetch s.home left join fetch s.away" +
            " where s.competition.id = :competitionId" +
            " order by s.round asc, s.number asc")
    List<SingleLegGame> findByCompetitionId(@Param("competitionId") Long competitionId);

    Optional<SingleLegGame> findByCompetitionIdAndRoundAndNumber(Long competitionId, Integer round, Integer match);

    @Query("select s from SingleLegGame s join fetch s.competition" +
            " left join fetch s.home left join fetch s.away" +
            " where s.id = :id and s.competition.id = :competitionId")
    Optional<SingleLegGame> findByIdAndCompetitionId(@Param("id") Long id, @Param("competitionId") Long competitionId);

    @Query(value = "select s from SingleLegGame s join fetch s.competition" +
            " left join fetch s.home left join fetch s.away" +
            " where s.competition.id = :competitionId" +
            " order by s.round asc, s.number asc",
            countQuery = "select count(s) from SingleLegGame s where s.competition.id = :competitionId")
    Page<SingleLegGame> findByCompetitionId(@Param("competitionId") Long competitionId, Pageable pageable);

    @Query("select s from SingleLegGame s join fetch s.competition" +
            " left join fetch s.home left join fetch s.away" +
            " where s.home.id = :homeId")
    List<SingleLegGame> findByHomeId(@Param("homeId") Long homeId);

    @Query("select s from SingleLegGame s join fetch s.competition" +
            " left join fetch s.home left join fetch s.away" +
            " where s.away.id = :awayId")
    List<SingleLegGame> findByAwayId(@Param("awayId") Long awayId);
}
