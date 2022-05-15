package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
}
