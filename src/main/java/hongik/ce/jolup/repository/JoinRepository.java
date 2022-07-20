package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JoinRepository extends JpaRepository<Join, Long> {
        List<Join> findByBelongId(Long belongId);
        List<Join> findByCompetitionId(Long competitionId);
        Optional<Join> findByBelongIdAndCompetitionId(Long belongId, Long competitionId);

        @Query(
                "SELECT j from Join j" +
                " where j.competition.id = :competitionId" +
                " order by j.result.points desc, j.result.goalDifference desc, j.result.goalFor desc, j.belong.member.name asc")
        List<Join> findByCompetitionIdSort(@Param("competitionId") Long competitionId);

}
