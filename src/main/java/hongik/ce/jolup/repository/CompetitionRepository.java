package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    List<Competition> findByRoomId(Long roomId);
    Optional<Competition> findByIdAndRoomId(Long id, Long roomId);
}
