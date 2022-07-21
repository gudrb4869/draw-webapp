package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.belong.Belong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BelongRepository extends JpaRepository<Belong, Long> {

    @Query("select b from Belong b join fetch b.member where b.member.id = :memberId")
    List<Belong> findByMemberId(@Param("memberId") Long memberId);
    Optional<Belong> findByMemberIdAndRoomId(Long memberId, Long roomId);
    Optional<Belong> findByIdAndRoomId(Long id, Long roomId);
}
