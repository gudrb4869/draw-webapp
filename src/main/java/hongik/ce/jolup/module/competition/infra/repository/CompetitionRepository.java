package hongik.ce.jolup.module.competition.infra.repository;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.room.domain.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    @EntityGraph(attributePaths = {"room"})
    Optional<Competition> findCompetitionWithRoomById(Long id);

    @EntityGraph(attributePaths = {"room"})
    Page<Competition> findCompetitionsByRoom(Room room, Pageable pageable);

    @EntityGraph(attributePaths = {"room", "participates"})
    Competition findCompetitionWithRoomAndParticipatesById(Long id);

    @EntityGraph(value = "Competition.withRoomAndParticipatesAndMembers")
    Optional<Competition> findCompetitionWithRoomAndParticipatesAndMembersById(Long id);

    @EntityGraph(attributePaths = {"room", "matches"})
    Competition findCompetitionWithRoomAndMatchesById(Long id);
}
