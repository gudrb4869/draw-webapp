package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.belong.Belong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BelongRepository extends JpaRepository<Belong, Long> {
}
