package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    List<Competition> findByRoomId(Long roomId);
}
