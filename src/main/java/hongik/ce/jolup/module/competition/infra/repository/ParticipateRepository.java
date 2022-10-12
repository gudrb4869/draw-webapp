package hongik.ce.jolup.module.competition.infra.repository;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipateRepository extends JpaRepository<Participate, Long> {
        @Query("SELECT l from Participate l left join fetch l.competition left join fetch l.member" +
                " where l.competition = :competition" +
                " order by l.win * 3 + l.draw desc, " +
                "l.goalFor - l.goalAgainst desc, l.goalFor desc, l.member.name asc")
        List<Participate> findLeagueRankingByCompetition(@Param("competition") Competition competition);
}
