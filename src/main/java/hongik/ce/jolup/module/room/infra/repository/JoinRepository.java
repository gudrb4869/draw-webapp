package hongik.ce.jolup.module.room.infra.repository;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface JoinRepository extends JpaRepository<Join, Long> {

    @Query("select j from Join j join fetch j.member join fetch j.room where j.member.id = :memberId")
    List<Join> findByMemberId(@Param("memberId") Long memberId);

    @Query(value = "select j from Join j join fetch j.member join fetch j.room where j.member.id = :memberId",
            countQuery = "select count(j) from Join j where j.member.id = :memberId")
    Page<Join> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @EntityGraph(attributePaths = {"member", "room"})
    Optional<Join> findByMemberIdAndRoomId(Long memberId, Long roomId);

    @Query("select j from Join j join fetch j.room join fetch j.member where j.id = :id and j.room.id = :roomId")
    Optional<Join> findByIdAndRoomId(@Param("id") Long id, @Param("roomId") Long roomId);

    @Query("select j from Join j join fetch j.member join fetch j.room where j.room.id = :roomId")
    List<Join> findByRoomId(@Param("roomId") Long roomId);

    @EntityGraph(attributePaths = {"member"})
    Page<Join> findByRoomId(@Param("roomId") Long roomId, Pageable pageable);

    @EntityGraph(value = "Join.withAll", type = EntityGraph.EntityGraphType.FETCH)
    Page<Join> findWithJoinByMemberId(Long memberId, Pageable pageable);

    boolean existsByRoomAndMember(Room room, Member member);

    Optional<Join> findByRoomAndMember(Room room, Member member);
}
