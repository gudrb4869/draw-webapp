package hongik.ce.jolup.module.competition.infra.repository;

import hongik.ce.jolup.module.competition.domain.entity.LeagueTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeagueTableRepository extends JpaRepository<LeagueTable, Long> {

        @Query("SELECT l from LeagueTable l join fetch l.competition join fetch l.member where l.member.id = :memberId")
        List<LeagueTable> findByMemberId(@Param("memberId") Long memberId);

        @Query("SELECT l from LeagueTable l join fetch l.competition left join fetch l.member where l.competition.id = :competitionId")
        List<LeagueTable> findByCompetitionId(@Param("competitionId") Long competitionId);

        @Query("SELECT l from LeagueTable l join fetch l.competition join fetch l.member" +
                " where l.member.id = :memberId and l.competition.id = :competitionId")
        Optional<LeagueTable> findByMemberIdAndCompetitionId(@Param("memberId") Long memberId, @Param("competitionId") Long competitionId);

        @Query("SELECT l from LeagueTable l join fetch l.competition left join fetch l.member" +
                " where l.competition.id = :competitionId" +
                " order by l.win * 3 + l.draw desc, " +
                "l.goalFor - l.goalAgainst desc, l.goalFor desc, l.member.name asc")
        List<LeagueTable> findByCompetitionIdSort(@Param("competitionId") Long competitionId);

}
