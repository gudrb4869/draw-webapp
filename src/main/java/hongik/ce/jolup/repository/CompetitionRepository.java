package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    @Override
    @Query("select c from Competition c join fetch c.room where c.id = :id")
    Optional<Competition> findById(@Param("id") Long id);

    @Query("select c from Competition c join fetch c.room where c.room.id = :roomId")
    List<Competition> findByRoomId(@Param("roomId") Long roomId);

    @Query("select c from Competition c join fetch c.room where c.id = :id and c.room.id = :roomId")
    Optional<Competition> findByIdAndRoomId(@Param("id") Long id, @Param("roomId") Long roomId);
}
