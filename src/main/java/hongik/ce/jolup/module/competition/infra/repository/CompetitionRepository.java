package hongik.ce.jolup.module.competition.infra.repository;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.room.domain.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    @Override
    @Query("select c from Competition c join fetch c.room where c.id = :id")
    Optional<Competition> findById(@Param("id") Long id);

    @Query(value = "select c from Competition c join fetch c.room where c.room.id = :roomId",
            countQuery = "select count(c) from Competition c where c.room.id = :roomId")
    Page<Competition> findByRoomId(@Param("roomId") Long roomId, Pageable pageable);

    @Query("select c from Competition c join fetch c.room where c.id = :id and c.room.id = :roomId")
    Optional<Competition> findByIdAndRoomId(@Param("id") Long id, @Param("roomId") Long roomId);

    Page<Competition> findByRoom(Room room, Pageable pageable);

    @EntityGraph(attributePaths = "room")
    Optional<Competition> findCompetitionById(Long id);
}
