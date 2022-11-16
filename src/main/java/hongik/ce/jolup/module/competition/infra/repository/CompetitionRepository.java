package hongik.ce.jolup.module.competition.infra.repository;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.room.domain.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    @EntityGraph(attributePaths = {"room"})
    Page<Competition> findCompetitionsByRoom(Room room, Pageable pageable);

    @EntityGraph(attributePaths = {"room", "participates"})
    Competition findCompetitionWithRoomAndParticipatesById(Long id);

    @EntityGraph(attributePaths = {"room", "matches"})
    Competition findCompetitionWithRoomAndMatchesById(Long id);
}
