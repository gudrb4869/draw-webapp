package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.join.Join;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JoinRepository extends JpaRepository<Join, Long> {

        @Query("SELECT j from Join j join fetch j.competition join fetch j.member where j.member.id = :memberId")
        List<Join> findByMemberId(@Param("memberId") Long memberId);

        @Query("SELECT j from Join j join fetch j.competition join fetch j.member where j.competition.id = :competitionId")
        List<Join> findByCompetitionId(@Param("competitionId") Long competitionId);

        @Query("SELECT j from Join j join fetch j.competition join fetch j.member" +
                " where j.member.id = :memberId and j.competition.id = :competitionId")
        Optional<Join> findByMemberIdAndCompetitionId(@Param("memberId") Long memberId, @Param("competitionId") Long competitionId);

        @Query("SELECT j from Join j join fetch j.competition join fetch j.member" +
                " where j.competition.id = :competitionId" +
                " order by j.result.points desc, j.result.goalDifference desc, j.result.goalFor desc, j.member.name asc")
        List<Join> findByCompetitionIdSort(@Param("competitionId") Long competitionId);

}
