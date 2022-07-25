package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.join.Join;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JoinRepository extends JpaRepository<Join, Long> {

        @Query("SELECT j from Join j join fetch j.competition join fetch j.belong where j.belong.id = :belongId")
        List<Join> findByBelongId(@Param("belongId") Long belongId);

        @Query("SELECT j from Join j join fetch j.competition join fetch j.belong where j.competition.id = :competitionId")
        List<Join> findByCompetitionId(@Param("competitionId") Long competitionId);

        @Query("SELECT j from Join j join fetch j.competition join fetch j.belong" +
                " where j.belong.id = :belongId and j.competition.id = :competitionId")
        Optional<Join> findByBelongIdAndCompetitionId(@Param("belongId") Long belongId, @Param("competitionId") Long competitionId);

        @Query("SELECT j from Join j join fetch j.competition join fetch j.belong" +
                " where j.competition.id = :competitionId" +
                " order by j.result.points desc, j.result.goalDifference desc, j.result.goalFor desc, j.belong.member.name asc")
        List<Join> findByCompetitionIdSort(@Param("competitionId") Long competitionId);

}
