package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.belong.Belong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BelongRepository extends JpaRepository<Belong, Long> {

    Optional<Belong> findByMemberIdAndRoomId(Long memberId, Long roomId);
    Optional<Belong> findByIdAndRoomId(Long id, Long roomId);
}
