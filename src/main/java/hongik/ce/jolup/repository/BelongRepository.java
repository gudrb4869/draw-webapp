package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.belong.Belong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BelongRepository extends JpaRepository<Belong, Long> {

    @Query(value = "select b from Belong b join fetch b.member join fetch b.room where b.member.id = :memberId",
            countQuery = "select count(b) from Belong b where b.member.id = :memberId")
    Page<Belong> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("select b from Belong b join fetch b.member join fetch b.room where b.member.id = :memberId and b.room.id = :roomId")
    Optional<Belong> findByMemberIdAndRoomId(@Param("memberId") Long memberId, @Param("roomId") Long roomId);

    @Query("select b from Belong b join fetch b.room join fetch b.member where b.id = :id and b.room.id = :roomId")
    Optional<Belong> findByIdAndRoomId(@Param("id") Long id, @Param("roomId") Long roomId);

    @Query("select b from Belong b join fetch b.member join fetch b.room where b.room.id = :roomId")
    List<Belong> findByRoomId(@Param("roomId") Long roomId);

    @Query(value = "select b from Belong b join fetch b.member join fetch b.room where b.room.id = :roomId",
            countQuery = "select count(b) from Belong b where b.room.id = :roomId")
    Page<Belong> findByRoomId(@Param("roomId") Long roomId, Pageable pageable);
}
