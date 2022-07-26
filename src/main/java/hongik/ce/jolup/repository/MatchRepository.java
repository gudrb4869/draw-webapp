package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("select m from Match m join fetch m.competition left join fetch m.home left join fetch m.away where m.competition.id = :competitionId")
    List<Match> findByCompetitionId(@Param("competitionId") Long competitionId);


    Optional<Match> findByCompetitionIdAndRoundNoAndMatchNo(Long competitionId, Integer roundNo, Integer matchNo);

    @Query("select m from Match m join fetch m.competition left join fetch m.home left join fetch m.away" +
            " where m.id = :id and m.competition.id = :competitionId")
    Optional<Match> findByIdAndCompetitionId(@Param("id") Long id, @Param("competitionId") Long competitionId);
}
